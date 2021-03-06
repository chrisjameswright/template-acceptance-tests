package uk.gov.hmrc.integration.cucumber.utils

import java.net.InetSocketAddress
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.auth.AuthType
import net.lightbody.bmp.{BrowserMobProxy, BrowserMobProxyServer}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions, FirefoxProfile}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}
import org.openqa.selenium.{Proxy, WebDriver}

import scala.collection.JavaConversions._


object SingletonDriver extends Driver

class Driver extends BrowserStackDriver {

  var instance: WebDriver = null
  private var baseWindowHandle: String = null
  var javascriptEnabled: Boolean = true

  def getInstance(): WebDriver = {
    if (instance == null || instance.toString.contains("(null)")) {
      initialiseBrowser()
    }
    instance
  }

  def initialiseBrowser() {

    instance = createBrowser()
    baseWindowHandle = instance.getWindowHandle
  }

  def closeInstance(): Unit = {
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

  val proxy: BrowserMobProxy = new BrowserMobProxyServer

  private def setProxy(): Proxy = {
    val proxySettingPattern = """(.+):(.+)@(.+):(\d+)""".r
    System.getProperty("qa.proxy") match {
      case proxySettingPattern(user, password, host, port) =>
        proxy.chainedProxyAuthorization(user, password, AuthType.BASIC)
        proxy.setChainedProxy(new InetSocketAddress(host, port.toInt))
      case _ => throw new RuntimeException("QA Proxy settings must be provided as username:password@proxyHost:proxyPortNumber")
    }
    proxy.setTrustAllServers(true)
    proxy.start()
    ClientUtil.createSeleniumProxy(proxy)
  }

  private def createBrowser(): WebDriver = {

    def createFirefoxDriver: WebDriver = {
      System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true")
      System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null")
      val profile = new FirefoxProfile()
      profile.setAcceptUntrustedCertificates(true)
      profile.setPreference("javascript.enabled", javascriptEnabled)

      val options = new FirefoxOptions()
      val capabilities = DesiredCapabilities.firefox()

      options.merge(capabilities)
      options.setProfile(profile)
      options.setAcceptInsecureCerts(true)

      new FirefoxDriver(options)
    }

    def createBrowserStackDriver = browserStackSetup(new DesiredCapabilities(getBrowserStackCapabilities))

    def createChromeDriver(headless: Boolean): WebDriver = {
      val options = new ChromeOptions()
      options.addArguments("test-type")
      options.addArguments("--no-sandbox")
      options.addArguments("start-maximized")
      options.addArguments("disable-infobars")
      val capabilities: DesiredCapabilities = DesiredCapabilities.chrome()
      if (Option(System.getProperty("qa.proxy")).isDefined) capabilities.setCapability(CapabilityType.PROXY, setProxy())
      options.setCapability("javascript.enabled", javascriptEnabled)
      options.merge(capabilities)
      if(headless) options.addArguments("headless")
      val driver = new ChromeDriver(options)
      driver
    }

    val environmentProperty = System.getProperty("browser", "firefox")
    environmentProperty match {
      case "firefox" => createFirefoxDriver
      case "browserstack" => createBrowserStackDriver
      case "chrome" => createChromeDriver(false)
      case "chrome-headless" => createChromeDriver(true)
      case _ => throw new IllegalArgumentException(s"Browser type not recognised: -D$environmentProperty")
    }
  }

}

