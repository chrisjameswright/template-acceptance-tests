package uk.gov.hmrc.integration.cucumber.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import uk.gov.hmrc.integration.cucumber.pages.BasePage._
import uk.gov.hmrc.integration.cucumber.utils.SeleniumBase

class BaseStepDef extends ScalaDsl with EN with SeleniumBase {

  And("""^I refresh the page$""") { () =>
    pageRefresh()
  }

  And("""^the (Continue|Submit) button is clicked$""") { (button: String) =>
    clickContinue()
  }

  And("""^the (Continue|Submit) button is clicked and confirm the page has validation errors$""") { (button: String) =>
    clickById("continue-button")
    verifyValidationErrorIsDisplayed()
  }

  Then("""^the browser is shutdown$""") { () =>
    ShutdownTest()
  }

  And("""^(the Submit button is clicked|I click the Continue button) and confirm the page shows the generic error message$""") { (button: String) =>
    clickContinueExpectFailure()
  }


}
