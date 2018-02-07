name := "vat-subscription-acceptance-tests"

version := "0.1.0"

scalaVersion := "2.11.7"


credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

val hmrcRepoHost = java.lang.System.getProperty("hmrc.repo.host", "https://nexus-preview.tax.service.gov.uk")

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers ++= Seq(
  "hmrc-snapshots" at hmrcRepoHost + "/content/repositories/hmrc-snapshots",
  "hmrc-releases" at hmrcRepoHost + "/content/repositories/hmrc-releases",
  "typesafe-releases" at hmrcRepoHost + "/content/repositories/typesafe-releases")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += Resolver.bintrayRepo("hmrc", "releases")

val useSelenium3 = java.lang.System.getProperty("useSelenium3", "true").toBoolean

lazy val selenium3 = Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "3.7.1",
  "org.seleniumhq.selenium" % "selenium-remote-driver" % "3.6.0",
  "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.6.0",
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "3.6.0",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0" % "test")

lazy val selenium2 = Seq(
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.1" % "test",
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "2.53.1",
  "org.seleniumhq.selenium" % "selenium-chrome-driver" % "2.53.1",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.52.0" % "test")

libraryDependencies ++= (if (useSelenium3) selenium3 else selenium2) ++ Seq(
  "com.typesafe.play" %% "play-json" % "2.3.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.pegdown" % "pegdown" % "1.1.0" % "test",
  "info.cukes" %% "cucumber-scala" % "1.2.4" % "test",
  "info.cukes" % "cucumber-junit" % "1.2.2" % "test",
  "info.cukes" % "cucumber-picocontainer" % "1.2.2" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.7.2",
  "log4j" % "log4j" % "1.2.17",
  "net.lightbody.bmp" % "browsermob-core" % "2.1.5",
  "uk.gov.hmrc" %% "zap-automation" % "0.17.0")

unmanagedJars in Compile += file("libs/harlib-1.1.1.jar")
unmanagedJars in Compile += file("libs/proxy-2.4.2-SNAPSHOT.jar")
unmanagedJars in Compile += file("libs/zap-api-2.4-v6.jar")