name := "template-acceptance-tests"

version := "0.1.0"

scalaVersion := "2.11.7"

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

val hmrcRepoHost = java.lang.System.getProperty("hmrc.repo.host", "https://nexus-preview.tax.service.gov.uk")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

resolvers ++= Seq(
  "hmrc-snapshots" at hmrcRepoHost + "/content/repositories/hmrc-snapshots",
  "hmrc-releases" at hmrcRepoHost + "/content/repositories/hmrc-releases",
  "typesafe-releases" at hmrcRepoHost + "/content/repositories/typesafe-releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  Resolver.bintrayRepo("hmrc", "releases"))

libraryDependencies ++= Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "3.7.1",
  "org.seleniumhq.selenium" % "selenium-remote-driver" % "3.7.1",
  "org.seleniumhq.selenium" % "selenium-chrome-driver" % "3.7.1",
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "3.7.1",
  "com.typesafe.play" %% "play-json" % "2.5.12",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.pegdown" % "pegdown" % "1.6.0" % "test",
  "info.cukes" %% "cucumber-scala" % "1.2.4" % "test",
  "info.cukes" % "cucumber-junit" % "1.2.4" % "test",
  "info.cukes" % "cucumber-picocontainer" % "1.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.9",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.8.9",
  "net.lightbody.bmp" % "browsermob-core" % "2.1.5",
  "uk.gov.hmrc" %% "zap-automation" % "0.19.0")

unmanagedJars in Compile += file("libs/harlib-1.1.1.jar")
unmanagedJars in Compile += file("libs/proxy-2.4.2-SNAPSHOT.jar")
unmanagedJars in Compile += file("libs/zap-api-2.4-v6.jar")