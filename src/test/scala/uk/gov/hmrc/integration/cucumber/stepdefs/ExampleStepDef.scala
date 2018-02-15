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
import uk.gov.hmrc.integration.cucumber.pages.ExamplePage._

class ExampleStepDef extends ScalaDsl with EN {

  When("""^A user wants to search the internet$"""){ () =>
  }

  When("""^the user opens Google$"""){ () =>
    navigateTo(url)
  }

  When("""^they see a search bar$"""){ () =>
    waitForPageToBeLoaded("lst-ib")
    clickById("lst-ib")
  }

}
