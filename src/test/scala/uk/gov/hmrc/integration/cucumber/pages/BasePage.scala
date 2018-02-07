package uk.gov.hmrc.integration.cucumber.pages

import java.net.URL
import java.util.concurrent.TimeUnit

import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, FluentWait}
import org.openqa.selenium.{WebElement, _}
import org.scalatest.Matchers
import uk.gov.hmrc.integration.cucumber.utils.Parameters._
import uk.gov.hmrc.integration.cucumber.utils.SingletonDriver

import scala.util.Try

object BasePage extends BasePage

trait BasePage extends Matchers {

  lazy val driver: WebDriver = SingletonDriver.getInstance()

  val fluentWait: FluentWait[WebDriver] = new FluentWait[WebDriver](BasePage.driver)
    .withTimeout(WAIT_TIME_OUT, TimeUnit.SECONDS)
    .pollingEvery(WAIT_POLLING_INTERVAL, TimeUnit.SECONDS)

  val shortFluentWait: FluentWait[WebDriver] = new FluentWait[WebDriver](BasePage.driver)
    .withTimeout(SHORT_WAIT_TIME, TimeUnit.SECONDS)
    .pollingEvery(POLLING_INTERVAL_SHORT_WAIT_TIME, TimeUnit.MILLISECONDS)

  def assertUrl(url: URL): Unit =
    fluentWait.until(ExpectedConditions.urlContains(url.getPath))

  def navigateTo(url: URL): Unit = {
    driver.navigate().to(url)
    assertUrl(url)
  }

  def navigateToIndex(url: URL): Unit = {
    driver.navigate().to(url)
  }


  def pageRefresh(): Unit = {
    try {
      driver.navigate().refresh()
    } catch {
      case uaEx: UnhandledAlertException =>
      case e: Exception => throw e
    }

    try {
      val alert = driver.switchTo().alert()
      alert.accept()
    }
    catch {
      case napEx: NoAlertPresentException =>
      case e: Exception => throw e
    }
  }

  def clickStubContinue() = {
    def continueButtonById = By.id("continue-button")

    fluentWait.until(ExpectedConditions.elementToBeClickable(continueButtonById))

    def continueButton = find(continueButtonById)

    fluentWait.until(ExpectedConditions.textToBePresentInElement(continueButton, "Stub user"))
    clickById("continue-button")
    fluentWait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("content"), "Successfully stubbed the following user"))
    fluentWait.until(ExpectedConditions.elementToBeClickable(continueButtonById))
    fluentWait.until(ExpectedConditions.textToBePresentInElement(continueButton, "Stub again"))
  }

  def currentUrl(): String = driver.getCurrentUrl

  def write(itemIdentifier: String, inputValue: String = "") {
    findById(itemIdentifier).clear()
    findById(itemIdentifier).sendKeys(inputValue)
  }

  // VERIFY METHODS

  def assertUrlNot(url: URL) = fluentWait.until(ExpectedConditions.not(ExpectedConditions.urlContains(url.getPath)))

  def verifyText(text: String): Unit = driver.findElement(By.cssSelector("h1")).getText shouldBe text

  def verifyValidationErrorIsDisplayed(): Unit = verifyElementVisible("error-summary-display")

  def verifyElementVisible(id: String): Unit = findById(id).isDisplayed shouldBe true

  def goBack() {
    clickContinue()
  }

  def verifyValue(element: String, inputText: String = "") {
          assert(findById(element).getAttribute("value") == inputText)
    }


  // CLICK METHODS

  def clickContinue(): Unit = {
    val currentURL = BasePage.driver.getCurrentUrl
    clickById("continue-button")
    fluentWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentURL)))
  }

  def clickNinoContinue(): Unit = {
    val currentURL = BasePage.driver.getCurrentUrl
    clickById("continue-button")
  }

  def clickSignOutButton(): Unit = {
    val currentURL = BasePage.driver.getCurrentUrl
    clickById("sign-out-button")
  }

  def clickContinueExpectFailure(): Unit = {
    clickById("continue-button")
    fluentWait.until(ExpectedConditions.textToBePresentInElement(findByCssSelector("h1"), errorTechnicalDifficulties))
  }

  def clickStart(): Unit = {
    val currentURL = BasePage.driver.getCurrentUrl
    clickById("start-button")
    fluentWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentURL)))
  }

  def clickById(id: String): Unit = clickBy(By.id(id))

  def clickBy(by: By): Unit = {
    val elem = find(by)

    val isRadioOrCheckbox = elem.getAttribute("type").equals("radio") || elem.getAttribute("type").equals("checkbox")

    if (isRadioOrCheckbox) {
      // In the new gov uk style the input itself may not be clickable
      // what the user actually clicks on is the label for the input
      try {
        val radioId = elem.getAttribute("id")
        val labelForRadio = findByCssSelector(s"""label[for*="$radioId"]""")

        fluentWait.until(ExpectedConditions.elementToBeClickable(labelForRadio))
      } catch {
        // however in the old hmrc style is the input itself that needs to be clicked on and on some services the
        // the label may not have a for attribute
        case e: NoSuchElementException =>
          fluentWait.until(ExpectedConditions.elementToBeClickable(elem))
      }
    } else {
      fluentWait.until(ExpectedConditions.elementToBeClickable(elem))
    }

    val isSelected = elem.getAttribute("selected") != null

    elem.click()
    // in IE8 radio buttons need a second click
    if (isRadioOrCheckbox && browserIsIE8()) {
      fluentWait.until(ExpectedConditions.elementToBeClickable(elem))
      elem.click()
    }

    if (isRadioOrCheckbox && !isSelected) {
      // a radio button can never be de-selected so we only care to maintain the state if it weren't previously
      fluentWait.until(ExpectedConditions.attributeToBeNotEmpty(elem, "selected"))
    }
  }

  // FIND & CHECK METHODS

  def find(by: By): WebElement = {
    val element: WebElement = driver.findElement(by)
    // jump to the element cos otherwise selenium may not function properly if the element is not visible on screen
    driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", element)
    fluentWait.until(ExpectedConditions.presenceOfElementLocated(by))
    driver.findElement(by)
  }

  def findByName(name: String): WebElement = find(By.name(name))

  def findById(name: String): WebElement = find(By.id(name))

  def findByCssSelector(selector: String): WebElement = find(By.cssSelector(selector))

  def sendKeysById(id: String, value: String): Unit = {
    fluentWait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
    val ele = findById(id)
    ele.clear()
    ele.sendKeys(value)
  }

  // OTHER METHODS

  def ShutdownTest(): Unit = driver.quit()

  class HmrcPageWaitException(exceptionMessage: String) extends Exception(exceptionMessage)

  def browserIsIE8(): Boolean = {
    val rwd: RemoteWebDriver = driver.asInstanceOf[RemoteWebDriver]
    val cap = rwd.getCapabilities

    try {
      (cap.getCapability("browserName").toString + "-" + cap.getCapability("version").toString) == "internet explorer-8"
    }
    catch {
      case _: Exception =>
        //        println("browserIsIE8 failed")
        //        println(s"browserName=${cap.getCapability("browserName")}")
        //        println(s"version=${cap.getCapability("version")}")
        false
    }
  }

}