package uk.gov.hmrc.integration.cucumber.utils

import com.typesafe.scalalogging.LazyLogging
import org.openqa.selenium.WebDriver
import uk.gov.hmrc.integration.cucumber.utils.drivers.{ChromeBrowser, FirefoxBrowser}

object Driver2 extends LazyLogging with WindowControls {

  val instance: WebDriver = createDriver()
  val baseWindowHandle: String = instance.getWindowHandle

  private def createDriver(): WebDriver = {
    val javascript: Boolean = javascriptEnabled()

    sys.props.get("browser").map(_.toLowerCase) match {
      case Some("chrome") => ChromeBrowser.initialise(javascript, sys.props.contains("headless"))
      case Some("chrome-headless") => ChromeBrowser.initialise(javascript, headlessMode = true)
      case Some("firefox") => FirefoxBrowser.initialise(javascript)
      case Some(name) => sys.error(s"'browser' property '$name' not recognised.")
      case None => {
        logger.warn("'browser' property is not set, defaulting to 'chrome'")
        ChromeBrowser.initialise(javascript, headlessMode = false)
      }
    }
  }

  private def javascriptEnabled(): Boolean = {
    sys.props.get("javascript").map(_.toLowerCase) match {
      case Some("true") => true
      case Some("false") => false
      case Some(_) => sys.error("'javascript' property must be 'true' or 'false'.")
      case None => {
        logger.warn("'javascript' property not set, defaulting to true.")
        true
      }
    }
  }
}
