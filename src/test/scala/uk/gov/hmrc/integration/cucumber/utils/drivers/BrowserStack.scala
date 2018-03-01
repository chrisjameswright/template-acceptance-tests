package uk.gov.hmrc.integration.cucumber.utils.drivers

import java.io.{FileNotFoundException, IOException}
import java.net.URL
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}

import scala.collection.JavaConversions._

import scala.io.Source

object BrowserStack {

  private val DRIVER_INFO_FLAG = false

  def browserStackSetup(): WebDriver = {

    val capabilities: DesiredCapabilities = getBrowserStackCapabilities()

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

    capabilities.setCapability("browserstack.debug", "true")
    capabilities.setCapability("browserstack.local", "true")
    capabilities.setCapability("project", "Template")
    capabilities.setCapability("build", "Template Build_1.0")

    val bsUrl = s"http://$userName:$automateKey@hub-cloud.browserstack.com/wd/hub"
    val driver = new RemoteWebDriver(new URL(bsUrl), capabilities)
    printCapabilities(driver, DRIVER_INFO_FLAG)
    driver
  }

  private def getBrowserStackCapabilities(): DesiredCapabilities = {
    val testDevice = System.getProperty("testDevice", "BS_Win8_Chrome_64")
    val resourceUrl = s"/browserstackdata/$testDevice.json"
    val cfgJsonString = Source.fromURL(getClass.getResource(resourceUrl)).mkString

    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    val capabilities: Map[String, Object] = mapper.readValue[Map[String, Object]](cfgJsonString)

    new DesiredCapabilities(capabilities)
  }

  private def printCapabilities(rwd: RemoteWebDriver, fullDump: Boolean): Unit = {
    var key = ""
    var value: Any = null

    println("RemoteWebDriver Basic Capabilities >>>>>>")
    val caps = rwd.getCapabilities
    val platform = caps.getPlatform
    println(s"platform : $platform")
    val browserName = caps.getBrowserName
    println(s"browserName : $browserName")
    val version = caps.getVersion
    println(s"version : $version")

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
  }

}
