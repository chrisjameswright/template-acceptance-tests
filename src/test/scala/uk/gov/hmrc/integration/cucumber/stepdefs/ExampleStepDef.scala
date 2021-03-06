/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.integration.cucumber.stepdefs

import cucumber.api.scala.{EN, ScalaDsl}
import uk.gov.hmrc.integration.cucumber.pages.{BasePage, ExamplePage}
import uk.gov.hmrc.integration.cucumber.pages.ExamplePage._

class ExampleStepDef extends ScalaDsl with EN {

  Given("""^A user wants to use the Auth Login Stub$""") { () =>
    navigateTo(url)
    checkPageHeading(header)
  }

  When("""^they enter valid auth criteria$""") { () =>
    sendKeysByName("authorityId", credId)
    sendKeysByName("redirectionUrl", url)
    clickByCSS("Input[value='Submit']")
  }

  Then("""^they are redirected to that service$""") { () =>
    checkPageHeading(header)
  }

}
