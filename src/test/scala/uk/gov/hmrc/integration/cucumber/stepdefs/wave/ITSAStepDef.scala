package uk.gov.hmrc.integration.cucumber.stepdefs.wave

import org.openqa.selenium.support.ui.WebDriverWait
import uk.gov.hmrc.integration.cucumber.utils.SingletonDriver


trait ITSASteps {
  implicit val driver = SingletonDriver.getInstance()
  implicit val waitFor = new WebDriverWait(driver, 10)
}