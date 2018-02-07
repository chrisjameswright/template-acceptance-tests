package uk.gov.hmrc.integration.cucumber.utils

import org.openqa.selenium._


trait SeleniumBase extends TearDown {

  def elementDisplayed(by: By): Boolean = {
    try {
      driver.findElement(by)
      true
    } catch {
      case e: NoSuchElementException => false
    }
  }

  def clickOn(selector: By) = driver.findElement(selector).click()

  def textFrom(selector: By) = driver.findElement(selector).getText

}
