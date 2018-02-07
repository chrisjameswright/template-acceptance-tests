package uk.gov.hmrc.integration.cucumber.utils

import java.io._

object WaveReport {

  def buildWaveHTMLReport(status: String, pageTitle: String, alertCount: Int, errorCount: Int, waveReportFlag: String, pageUrl: String): Unit = {
    if (waveReportFlag == "N") {
      val writer = new PrintWriter("target/wave-reports/index.html", "UTF-8")
      writer.println("<tr>")
      writer.println( s"""<td ><a href="${pageUrl}">${pageTitle}</a></td>""")
      writer.println( s"""<td  style="background-color: #FFD700;text-align:center;">${alertCount}</td>""")
      writer.println( s"""<td style="background-color: #FF0000;text-align:center;">${errorCount}</td>""")
      if (status == "Failed") {
        writer.println( s"""<td style="background-color: #D88A8A;text-align:center;">${status}</td>""")
      }
      else {
        writer.println( s"""<td style="background-color: #C5D88A;text-align:center;">${status}</td>""")
      }
      writer.println("</tr>")
      writer.close()
    }
    else {
      val writer = new PrintWriter(new BufferedWriter(new FileWriter("target/wave-reports/index.html", true)))
      writer.println("<tr>")
      writer.println( s"""<td ><a href="${pageUrl}">${pageTitle}</a></td>""")
      writer.println( s"""<td  style="background-color: #FFD700;text-align:center;">${alertCount}</td>""")
      writer.println( s"""<td style="background-color: #FF0000;text-align:center;">${errorCount}</td>""")
      if (status == "Failed") {
        writer.println( s"""<td style="background-color: #D88A8A;text-align:center;">${status}</td>""")
      }
      else {
        writer.println( s"""<td style="background-color: #C5D88A;text-align:center;">${status}</td>""")
      }
      writer.println("</tr>")
      writer.close()
    }
  }

  def createAccessiblityReport(): Unit = {
    val headerText = "\n<!DOCTYPE html>\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n\t<title>Wave Accessibility Test Report</title>\n\n</head>\n<body id=\"top\" bgcolor=\"#E6E6FA\">\n\t<div id=\"fullwidth_header\">\n\t\t<div class=\"container_12\">\n\t\t\t<h1 style=\"text-align:center;\"><font color=\"#228B22\" size=\"30\">Wave Accessibility Test Report</font></h1>\n\t\t\t<img src=\"wavelogo.png\" alt=\"Wave Logo\" style=\"width:304px;height:100px;\">\n\t\t<div>\n\t</div>\n\n </td>\n </tr>\n </table>\n</div>\n\t<br/>\n\t\t<div class=\"grid_12 hr\"></div>\n\n\t<div>\n\t  <br/>\n  <h2 ><font color=\"#228B22\">Feature Statistics</font></h2>\n  <table style=\"border-collapse:collapse;\" border=\"1\">\n\n<tr>\n    <th style=\"text-align:left;\">Page Title</th>\n    <th style=\"text-align:left;\">Alerts</th>\n    <th style=\"text-align:left;\">Errors</th>\n    <th style=\"text-align:left;\">Status</th>\n\n</tr>"
    val footerText = "</tr></table>\n\n<div style=\"padding:40px\">\n</div>\n</div>\n\n\n\n</body>\n</html>"
    val reader = new BufferedReader(new FileReader("target/wave-reports/index.html"))

    val sb = new StringBuilder()
    var line = reader.readLine()

    while (line != null) {
      sb.append(line)
      sb.append(System.lineSeparator())
      line = reader.readLine()
    }
    val tableContent = sb.toString
    val fileContent = headerText + tableContent + footerText
    val writer = new PrintWriter("target/wave-reports/index.html", "UTF-8")
    writer.println(fileContent)
    writer.close()

  }
}
