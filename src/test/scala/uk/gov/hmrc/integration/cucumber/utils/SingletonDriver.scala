package uk.gov.hmrc.integration.cucumber.utils

import java.io.{FileNotFoundException, IOException}
import java.net.{InetSocketAddress, URL}
import java.util
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.auth.AuthType
import net.lightbody.bmp.{BrowserMobProxy, BrowserMobProxyServer}
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions, FirefoxProfile}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.{Proxy, WebDriver}

import scala.collection.JavaConversions._
import scala.io.Source

import scala.language.postfixOps


object SingletonDriver extends Driver

class Driver {

  import ZapRunner._

  private val SAUCY = "saucy"

  // set flag to true to see full information about browserstack webdrivers on initialisation
  private val DRIVER_INFO_FLAG = false

  var instance: WebDriver = null
  private var baseWindowHandle: String = null
  var javascriptEnabled: Boolean = true

  def setJavascript(enabled: Boolean) {
    javascriptEnabled = enabled
    if (instance != null) closeInstance()
  }

  val networkProxyPort = 8080

  def getInstance(): WebDriver = {
    lazy val hasQuit: Boolean = instance.toString.contains("(null)")

    if (instance == null || hasQuit) {
      initialiseBrowser()

    }
    instance
  }

  def initialiseBrowser() {
    instance = createBrowser()
    baseWindowHandle = instance.getWindowHandle
  }

  def closeInstance() = {
    if (instance != null) {

      closeNewlyOpenedWindows()

      instance.close()
      instance = null
      baseWindowHandle = null
    }
  }

  def closeNewlyOpenedWindows() {
    instance.getWindowHandles.toList.foreach(w =>
      if (w != baseWindowHandle) instance.switchTo().window(w).close()
    )

    instance.switchTo().window(baseWindowHandle)
  }

  lazy private val seleniumProxy: Option[Proxy] = initProxy()

  private def initProxy(): Option[Proxy] = {
    if (Option(System.getProperty("qa.proxy")).isDefined) {
      val proxy: BrowserMobProxy = new BrowserMobProxyServer()
      val proxySettingPattern = """(.+):(.+)@(.+):(\d+)""".r
      System.getProperty("qa.proxy") match {
        case proxySettingPattern(user, password, host, port) =>
          proxy.chainedProxyAuthorization(user, password, AuthType.BASIC)
          proxy.setChainedProxy(new InetSocketAddress(host, port.toInt))
        case _ => throw new RuntimeException("QA Proxy settings must be provided as username:password@proxyHost:proxyPortNumber")
      }
      proxy.setTrustAllServers(true)
      proxy.start()
      Some(ClientUtil.createSeleniumProxy(proxy))
    }
    else None
  }

  private def createBrowser(): WebDriver = {

    def createFirefoxDriver: WebDriver = {
      val options: FirefoxOptions = new FirefoxOptions()
      val profile: FirefoxProfile = new FirefoxProfile()

      options.setProfile(profile)
      options.setAcceptInsecureCerts(true)
      options.addArguments("--start-maximized")
      profile.setAcceptUntrustedCertificates(true)
      profile.setPreference("javascript.enabled", javascriptEnabled)

      val capabilities = DesiredCapabilities.firefox()
      capabilities.setCapability("takesScreenshot", true)

      System.setProperty("webdriver.gecko.driver", "drivers/geckodriver")
      System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true")
      System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")

      if (seleniumProxy isDefined) capabilities.setCapability(CapabilityType.PROXY, seleniumProxy.get)

      val driver = new FirefoxDriver(options)
      val caps = driver.getCapabilities
      val browserName = caps.getBrowserName
      val browserVersion = caps.getVersion
      println(s"Browser name: $browserName, Version: $browserVersion")
      driver
    }

    lazy val systemProperties = System.getProperties
    lazy val isMac: Boolean = getOs.startsWith("Mac")
    lazy val isLinux: Boolean = getOs.startsWith("Linux")
    lazy val linuxArch = systemProperties.getProperty("os.arch")
    lazy val isWindows: Boolean = getOs.startsWith("Windows")

    def getOs = System.getProperty("os.name")

    def createChromeDriver: WebDriver = {
      println(s"JAVASCRIPT ENABLED: $javascriptEnabled")
      if (StringUtils.isEmpty(systemProperties.getProperty("webdriver.chrome.driver"))) {
        if (isMac)
          systemProperties.setProperty("webdriver.chrome.driver", "drivers/chromedriver_mac64")
        else if (isLinux && linuxArch == "amd32")
          systemProperties.setProperty("webdriver.chrome.driver", "drivers/chromedriver_linux32")
        else if (isWindows)
          System.setProperty("webdriver.chrome.driver", "drivers//chromedriver_win32")
        else
          systemProperties.setProperty("webdriver.chrome.driver", "drivers/chromedriver_linux64")
      }
      val options = new ChromeOptions()
      val capabilities: DesiredCapabilities = DesiredCapabilities.chrome()
      if (Option(System.getProperty("qa.proxy")).isDefined) capabilities.setCapability(CapabilityType.PROXY, initProxy())
      options.setCapability("takesScreenshot", true)
      options.setCapability("javascript.enabled", javascriptEnabled)
      options.addArguments("--start-maximized")
      capabilities.setCapability(ChromeOptions.CAPABILITY, options)
      if (seleniumProxy isDefined) capabilities.setCapability(CapabilityType.PROXY, seleniumProxy.get)
      options.merge(capabilities)
      val driver = new ChromeDriver(options)
      driver
    }


    def createHMTLUnitDriver: WebDriver = {
      new HtmlUnitDriver()
    }

    def createSaucyDriver: WebDriver = {
      val capabilities = DesiredCapabilities.firefox()
      capabilities.setCapability("version", "22")
      capabilities.setCapability("platform", "OS X 10.9")

      capabilities.setCapability("name", "Frontend Integration") // TODO: should we add a timestamp here?
      capabilities.setCapability("takesScreenshot", true)

      //Need to enquire about this website.
      new RemoteWebDriver(
        new java.net.URL("http://Optimus:3e4f3978-2b40-4965-a6b3-4fb7243bc1f2@ondemand.saucelabs.com:80/wd/hub"), //
        capabilities)
    }

    def createBrowserStackDriver: WebDriver = {

      var userName: String = null
      var automateKey: String = null

      try {
        val prop: Properties = new Properties()
        prop.load(this.getClass.getResourceAsStream("/browserConfig.properties"))

        userName = prop.getProperty("username")
        automateKey = prop.getProperty("automatekey")
      }
      catch {
        case e: FileNotFoundException => e.printStackTrace();
        case e: IOException => e.printStackTrace();
      }

      // create capabilities with device/browser settings from config file
      val bsCaps = getBrowserStackCapabilities
      val desCaps = new DesiredCapabilities(bsCaps)

      // set additional generic capabilities
      desCaps.setCapability("browserstack.debug", "true")
      desCaps.setCapability("browserstack.local", "true")
      desCaps.setCapability("project", "Template")
      desCaps.setCapability("build", "Template Build_1.0") //?????

      val bsUrl = s"http://$userName:$automateKey@hub-cloud.browserstack.com/wd/hub"
      val rwd = new RemoteWebDriver(new URL(bsUrl), desCaps)
      printCapabilities(rwd, DRIVER_INFO_FLAG)
      rwd
    }

    def getBrowserStackCapabilities: Map[String, Object] = {
      val testDevice = System.getProperty("testDevice", "BS_Win8_Chrome_64")
      val resourceUrl = s"/browserstackdata/$testDevice.json"
      val cfgJsonString = Source.fromURL(getClass.getResource(resourceUrl)).mkString

      val mapper = new ObjectMapper() with ScalaObjectMapper
      mapper.registerModule(DefaultScalaModule)
      mapper.readValue[Map[String, Object]](cfgJsonString)
    }

    def createChromeHeadlessDriver: WebDriver = {
      System.setProperty("webdriver.chrome.driver", "drivers/chromedriver_linux64")

      val options = new ChromeOptions()
      val capabilities = DesiredCapabilities.chrome()
      options.addArguments("headless")
      capabilities.setCapability(ChromeOptions.CAPABILITY, options)
      val driver = new ChromeDriver(options)
      val caps = driver.getCapabilities
      val browserName = caps.getBrowserName
      val browserVersion = caps.getVersion
      println(s"Browser name: $browserName, Version: $browserVersion")
      driver
    }

    def printCapabilities(rwd: RemoteWebDriver, fullDump: Boolean): Unit = {
      var key = ""
      var value: Any = null

      println("RemoteWebDriver Basic Capabilities >>>>>>")
      // step 1, print out the common caps which have getters
      val caps = rwd.getCapabilities
      val platform = caps.getPlatform
      println(s"platform : $platform")
      val browserName = caps.getBrowserName
      println(s"browserName : $browserName")
      val version = caps.getVersion
      println(s"version : $version")

      // step 2, print out common caps which need to be explicitly retrieved using their key
      val capsMap = caps.asMap()
      val basicKeyList = List("os", "os_version", "mobile", "device", "deviceName")
      for (key <- basicKeyList) {
        if (capsMap.containsKey(key)) {
          value = capsMap.get(key)
          println(s"$key : $value")
        } else {
          println(s"$key : not set")

        }
      }

      if (fullDump) {
        // step 3, if requested, dump everything
        println("Full Details >>>>>>")
        for (key <- capsMap.keySet()) {
          value = capsMap.get(key)
          println(s"$key : $value")
        }
      }
    }

    def createZapFirefoxDriver: WebDriver = {
      val options: FirefoxOptions = new FirefoxOptions()
      val profile: FirefoxProfile = new FirefoxProfile()

      profile.setPreference("javascript.enabled", javascriptEnabled)
      profile.setAcceptUntrustedCertificates(true)
      profile.setPreference("network.proxy.type", 1)
      profile.setPreference("network.proxy.http", "localhost")
      profile.setPreference("network.proxy.http_port", zapProxyPort)
      profile.setPreference("network.proxy.share_proxy_settings", true)
      profile.setPreference("network.proxy.no_proxies_on", "")

      val capabilities = DesiredCapabilities.firefox()
      capabilities.setCapability("takesScreenshot", true)

      System.setProperty("webdriver.gecko.driver", "drivers/geckodriver")
      System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true")
      System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")

      //      if (seleniumProxy isDefined) capabilities.setCapability(CapabilityType.PROXY, seleniumProxy.get)

      options.setProfile(profile)
      options.setAcceptInsecureCerts(true)
      options.addArguments("--start-maximized")
      options.merge(capabilities)

      val driver = new FirefoxDriver(options)
      val caps = driver.getCapabilities
      val browserName = caps.getBrowserName
      val browserVersion = caps.getVersion

      println(s"Running ZAP Firefox Driver on $browserName, Version: $browserVersion")
      driver
    }

    def createZapChromeDriver: WebDriver = {
      if (StringUtils.isEmpty(systemProperties.getProperty("webdriver.chrome.driver"))) {
        if (isMac)
          systemProperties.setProperty("webdriver.chrome.driver", "drivers/chromedriver_mac64")
        else if (isLinux && linuxArch == "amd32")
          systemProperties.setProperty("webdriver.chrome.driver", "drivers/chromedriver_linux32")
        else if (isWindows)
          System.setProperty("webdriver.chrome.driver", "drivers//chromedriver_win32")
        else
          systemProperties.setProperty("webdriver.chrome.driver", "drivers/chromedriver_linux64")
      }

      var options = new ChromeOptions()
      var capabilities = DesiredCapabilities.chrome()
      // need to have "--test-type" to allow "bad flags"
      // see https://stackoverflow.com/questions/41566892/chromeoptions-ignore-certificate-errors-does-not-get-rid-of-err-cert-authori
      options.addArguments("test-type")
      // to ignore the ssl cert checks (this is a bad flag)
      options.addArguments("ignore-certificate-error")
      options.setExperimentalOption("excludeSwitches", util.Arrays.asList("ignore-certificate-errors"))
      options.addArguments("--start-maximized")

      val proxy = new Proxy()
      proxy.setHttpProxy(s"http://localhost:$zapProxyPort")
      capabilities.setCapability("proxy", proxy)
      capabilities.setCapability(ChromeOptions.CAPABILITY, options)
      options.merge(capabilities)
      val driver = new ChromeDriver(options)
      val caps = driver.getCapabilities
      val browserName = caps.getBrowserName
      val browserVersion = caps.getVersion
      println("Browser name & version: " + browserName + " " + browserVersion)
      driver
    }

    val environmentProperty = System.getProperty("browser", "firefox")
    environmentProperty match {
      case "firefox" => createFirefoxDriver
      case "browserstack" => createBrowserStackDriver
      case "htmlunit" => createHMTLUnitDriver
      case "chrome" => createChromeDriver
      case "zap-firefox" => createZapFirefoxDriver
      case "zap-chrome" => createZapChromeDriver
      case "chrome-headless" => createChromeHeadlessDriver
      case SAUCY => createSaucyDriver
      case _ => throw new IllegalArgumentException(s"Browser type not recognised: -D$environmentProperty")
    }
  }

}

