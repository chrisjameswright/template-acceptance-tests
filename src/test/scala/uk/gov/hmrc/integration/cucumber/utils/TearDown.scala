package uk.gov.hmrc.integration.cucumber.utils

import cucumber.api.Scenario
import cucumber.api.java.{After, Before}
import org.openqa.selenium.{OutputType, TakesScreenshot, WebDriver, WebDriverException}


trait TearDown {

  lazy val driver: WebDriver = SingletonDriver.instance

  lazy val environmentProperty: String = System.getProperty("environment", "local").toLowerCase

  //  @Before
  //  def initialize() {
  //    //      driver.manage().deleteAllCookies()
  //  }

  @After
  def tearDown(result: Scenario) {
    if (result.isFailed) {
      if (driver.isInstanceOf[TakesScreenshot]) {
        try {
          val screenshot = driver.asInstanceOf[TakesScreenshot].getScreenshotAs(OutputType.BYTES)
          result.embed(screenshot, "image/png")
        } catch {
          case somePlatformsDontSupportScreenshots: WebDriverException => System.err.println(somePlatformsDontSupportScreenshots.getMessage)
        }
      }
    }
  }

  @After(Array("@Shutdown"))
  def afterShutDown(result: Scenario): Unit = {
    if (environmentProperty == "local") {
      import sys.process._
      val maxLinesReturned = 100000
      lazy val feLogs: String = ("sm -l INCOME_TAX_SUBSCRIPTION_FRONTEND" #| s"tail -n $maxLinesReturned" !!)
      result.write("INCOME_TAX_SUBSCRIPTION_FRONTEND logs:\n")
      result.write(feLogs)
    }
  }

}
