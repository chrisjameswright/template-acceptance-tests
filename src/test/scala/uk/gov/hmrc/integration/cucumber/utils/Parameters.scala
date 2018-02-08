package uk.gov.hmrc.integration.cucumber.utils

object Parameters {

  //***************************************************************************************//
  //******************************* CONFIGURABLE PARAMETERS *******************************//
  //***************************************************************************************//

  // Fluent Wait settings
  val WAIT_POLLING_INTERVAL	= 5
  val WAIT_TIME_OUT		      = 20
  val LONG_WAIT_TIME		                = 40  // seconds
  val POLLING_INTERVAL_LONG_WAIT_TIME	  = 5   // seconds
  val SHORT_WAIT_TIME		                = 3   // seconds
  val POLLING_INTERVAL_SHORT_WAIT_TIME	= 250 // milliseconds

  //**************************************************************************************//
  //********************************** ERROR PARAMETERS **********************************//
  //**************************************************************************************//

  // Technical difficulties page
  val errorTechnicalDifficulties  = "Sorry, we’re experiencing technical difficulties"

}