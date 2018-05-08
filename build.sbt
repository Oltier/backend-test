name := "backend-technical-test"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "joda-time" % "joda-time" % "2.9.9"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.12"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.1"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.12"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1"

libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.1.1"

libraryDependencies ++= Seq(
//  "junit"          % "junit"     % "4.9"   withSources(),
//  "org.scalatest" %% "scalatest" % "1.6.1" withSources(),
//  "org.specs2"    %% "specs2"    % "1.5"   withSources()
//  "org.specs2" %% "specs2" % "1.12.4" % Test,
//  "org.scalactic" %% "scalactic" % "3.0.5",
//  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

mainClass in assembly := Some("ciklum.backend.test.Entry")

