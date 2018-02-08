//package uk.gov.hmrc.integration.cucumber.utils
//
//import java.net.URL
//
//import scala.sys.process._
//
//object ServiceURLs {
//
//  private implicit def stringToUrl(urlString: String): URL = new URL(urlString)
//
//  val LOCAL_BASEURL: URL = "http://localhost:9561/report-quarterly/income-and-expenses/sign-up"
//  val PREVIEW_BASEURL: URL = "https://preview.tax.service.gov.uk/report-quarterly/income-and-expenses/sign-up"
//  val QA_BASEURL: URL = "https://www.qa.tax.service.gov.uk/report-quarterly/income-and-expenses/sign-up"
//  val STAGING_BASEURL: URL = "https://www.staging.tax.service.gov.uk/report-quarterly/income-and-expenses/sign-up"
//  val DEV_BASEURL: URL = "https://www.development.tax.service.gov.uk/report-quarterly/income-and-expenses/sign-up"
//
//  val LOCAL_GGURL: URL = "http://localhost:9025/gg/sign-in"
//  val PREVIEW_GGURL: URL = "https://preview.tax.service.gov.uk/gg/sign-in"
//  val QA_GGURL: URL = "https://www.qa.tax.service.gov.uk/gg/sign-in"
//  val STAGING_GGURL: URL = "https://www.staging.tax.service.gov.uk/gg/sign-in"
//  val DEV_GGURL: URL = "https://www.development.tax.service.gov.uk/gg/sign-in"
//
//  val LOCAL_AUTHURL: URL = "http://localhost:9949/"
//  val DEV_AUTHURL: URL = "https://www.development.tax.service.gov.uk/"
//  val QA_AUTHURL: URL = "https://www.qa.tax.service.gov.uk/"
//  val STAGING_AUTHURL: URL = "https://www.staging.tax.service.gov.uk/"
//
//  val LOCAL_REDIRECTIONURL: URL = "http://localhost:9561/report-quarterly/income-and-expenses/sign-up/"
//  val DEV_REDIRECTIONURL: URL = "https://www.development.tax.service.gov.uk/report-quarterly/income-and-expenses/sign-up/"
//  val QA_REDIRECTIONURL: URL = "https://www.qa.tax.service.gov.uk/report-quarterly/income-and-expenses/sign-up/"
//  val STAGING_REDIRECTIONURL: URL = "https://www.staging.tax.service.gov.uk/report-quarterly/income-and-expenses/sign-up/"
//
//  val LOCAL_PREFERENCEURL: URL = "http://localhost:9024"
//  val DEV_PREFERENCEURL: URL = "https://www.development.tax.service.gov.uk"
//  val QA_PREFERENCEURL: URL = "https://www.qa.tax.service.gov.uk"
//  val STAGING_PREFERENCEURL: URL = "https://www.staging.tax.service.gov.uk"
//
//  // BROWSERSTACK - Switch Base URLs to use IP address rather than host URL for Browserstack testing of Phones and Pads (all other testing is done via URL but Phones and Pads only work with IPs).
//  var testDevice = System.getProperty("testDevice")
//  val BROWSERSTACK_IP = getHostIP
//
//  // Detect Phones / Pads that work on Browserstack.com website. Note - most don't, which is why they aren't included here.
//  var PHONE_OR_PAD = testDevice match {
//    case "BS_iOS_iPhone5S_v7" => true
//    case "iPad_Air_v8_3" | "iPad_Mini_v7" => true
//    case _ => false
//  }
//
//  // BROWSERSTACK BASE URLs -> for Phones and Pads (these only work when the host address is replaced with an IP address)
//  val LOCAL_BROWSERSTACK_BASEURL = "http://" + BROWSERSTACK_IP + ":9561/report-quarterly/income-and-expenses/sign-up"
//  val LOCAL_BROWSERSTACK_PREFERENCEURL = "http://" + BROWSERSTACK_IP + ":9024"
//  val LOCAL_BROWSERSTACK_AUTHURL = "http://" + BROWSERSTACK_IP + ":9949/"
//  val LOCAL_BROWSERSTACK_GGURL = "http://" + BROWSERSTACK_IP + ":9025/gg/sign-in"
//  val LOCAL_BROWSERSTACK_REDIRECTIONURL = "http://" + BROWSERSTACK_IP + ":9561/report-quarterly/income-and-expenses/sign-up"
//
//
//  lazy val environmentProperty: String = System.getProperty("environment", "local").toLowerCase
//
//  def getHostIP = "hostname -I".!!.takeWhile(_ != ' ')
//
//  lazy val basePageUrl: URL = {
//    environmentProperty match {
//      case "local" => if (!PHONE_OR_PAD) LOCAL_BASEURL else LOCAL_BROWSERSTACK_BASEURL
//      case "preview" => PREVIEW_BASEURL
//      case "qa" => QA_BASEURL
//      case "staging" => STAGING_BASEURL
//      case "dev" => DEV_BASEURL
//      case _ => throw new IllegalArgumentException(s"Environment '$environmentProperty' not known")
//    }
//  }
//
//  lazy val ggSignInUrl: URL = {
//    environmentProperty match {
//      case "local" => if (!PHONE_OR_PAD) LOCAL_GGURL else LOCAL_BROWSERSTACK_GGURL
//      case "preview" => PREVIEW_GGURL
//      case "qa" => QA_GGURL
//      case "staging" => STAGING_GGURL
//      case "dev" => DEV_GGURL
//      case _ => throw new IllegalArgumentException(s"Environment '$environmentProperty' not known")
//    }
//  }
//
//  lazy val authUrl: URL = {
//    environmentProperty match {
//      case "local" => if (!PHONE_OR_PAD) LOCAL_AUTHURL else LOCAL_BROWSERSTACK_AUTHURL
//      case "dev" => DEV_AUTHURL
//      case "qa" => QA_AUTHURL
//      case "staging" => STAGING_AUTHURL
//      case _ => throw new IllegalArgumentException(s"Environment '$environmentProperty' not known")
//    }
//  }
//
//  lazy val redirectionUrl: URL = {
//    environmentProperty match {
//      case "local" => if (!PHONE_OR_PAD) LOCAL_REDIRECTIONURL else LOCAL_BROWSERSTACK_REDIRECTIONURL
//      case "dev" => DEV_REDIRECTIONURL
//      case "qa" => QA_REDIRECTIONURL
//      case "staging" => STAGING_REDIRECTIONURL
//      case _ => throw new IllegalArgumentException(s"Environment '$environmentProperty' not known")
//    }
//  }
//
//  lazy val preferenceUrl: URL = {
//    environmentProperty match {
//      case "local" => LOCAL_PREFERENCEURL
//      case "dev" => DEV_PREFERENCEURL
//      case "qa" => QA_PREFERENCEURL
//      case "staging" => STAGING_PREFERENCEURL
//      case _ => throw new IllegalArgumentException(s"Environment '$environmentProperty' not known")
//    }
//  }
//
//  private def escapeUrl(url: String) = java.net.URLEncoder.encode(url, "utf-8")
//
//  val GG_REDIRECTION_QUERY_STRING: String =
//    "?continue=" + escapeUrl(basePageUrl + "/index") + "&origin=" + escapeUrl("vat-subscription-frontend")
//
//
//  lazy val agtBasePageUrl: URL = basePageUrl + "/client"
//
//  object IndividualURLs {
//
//    lazy val urlStart: URL = basePageUrl + "/"
//    lazy val urlIndex: URL = basePageUrl + "/index"
//    lazy val urlUserDetails: URL = basePageUrl + "/user-details"
//    lazy val urlUserCheckYourAnswers: URL = basePageUrl + "/confirm-user"
//    lazy val urlUserConfirmError: URL = basePageUrl + "/error/user-details"
//    lazy val urlIncome: URL = basePageUrl + "/income"
//    lazy val urlPropertyIncome: URL = basePageUrl + "/property/income"
//    lazy val urlIncomeOther: URL = basePageUrl + "/income-other"
//    lazy val urlErrorOtherIncome: URL = basePageUrl + "/other-income-in-final-report"
//    lazy val urlAccountingPeriodDates: URL = basePageUrl + "/business/accounting-period-dates"
//    lazy val urlMatchToTaxYear: URL = basePageUrl + "/business/match-to-tax-year"
//    lazy val urlBusinessPeriodDates: URL = basePageUrl + "/business-period-dates"
//    lazy val urlBusinessName: URL = basePageUrl + "/business/name"
//    lazy val urlBusinessAccountingMethod: URL = basePageUrl + "/business/accounting-method"
//    lazy val urlTerms: URL = basePageUrl + "/terms"
//    lazy val urlCheckYourAnswers: URL = basePageUrl + "/check-your-answers"
//    lazy val urlSoleTrader: URL = basePageUrl + "/business/sole-trader"
//    lazy val urlNotEligible: URL = basePageUrl + "/not-eligible"
//    lazy val urlConfirmation: URL = basePageUrl + "/confirmation"
//    lazy val urlAlreadyEnrolled: URL = basePageUrl + "/already-enrolled"
//    lazy val urlNoNino: URL = basePageUrl + "/error/no-nino"
//    lazy val urlPreferencesOnPaperlessInitial: URL = preferenceUrl + "/paperless/choose/mtdfbit"
//    lazy val urlPreferencesPaperlessDone: URL = preferenceUrl + "/paperless/choose/nearly-done"
//    lazy val urlPreferencesPaperlessCallBack: URL = basePageUrl + "/paperless-error"
//    lazy val urlAuthStubSignIn: URL = authUrl + "auth-login-stub/gg-sign-in"
//    lazy val urlggSignInUrl: URL = ggSignInUrl
//    lazy val urlExitSurvey: URL = basePageUrl + "/exit-survey"
//    lazy val urlFeedbackConfirmation: URL = basePageUrl + "/feedback-submitted"
//    lazy val urlGovUK: URL = "https://www.gov.uk/"
//    lazy val urlSignOut: URL = basePageUrl + "/logout"
//    lazy val urlClaimSubscription: URL = basePageUrl + "/claim-subscription"
//    lazy val urlAffinityGroupError: URL = basePageUrl + "/error/affinity-group"
//    lazy val urlLockedOut: URL = basePageUrl + "/error/lockout"
//    lazy val urlIVFailure: URL = basePageUrl + "/error/iv-failed"
//    lazy val urlConfirmAgent: URL = basePageUrl + "/confirm-agent"
//    lazy val urlAuthoriseAgent: URL = basePageUrl + "/authorise-agent"
//    lazy val urlRegisterForSA: URL = basePageUrl + "/register-for-SA"
//    lazy val urlCannotReportYet: URL = basePageUrl + "/error/cannot-report-yet"
//    lazy val urlRentUkProperty: URL = basePageUrl + "/rent-uk-property"
//    lazy val urlWorkForYourself: URL = basePageUrl + "/work-for-yourself"
//
//  }
//
//   object TestOnly {
//
//    lazy val urlClearPreferences: URL = basePageUrl + "/test-only/clear-preferences"
//    lazy val urlClearNinoPreferences: URL = basePageUrl + "/test-only/clear-preferences-for"
//    lazy val clearEnrolmentUrl: URL = basePageUrl + "/test-only/reset-users"
//    lazy val urlResetUserLockout: URL = basePageUrl + "/test-only/reset-lockout"
//    lazy val urlUserStubService: URL = basePageUrl + "/test-only/stub-user"
//    lazy val urlFeatureSwitch: URL = basePageUrl + "/test-only/feature-switch"
//
//  }
//
//}
