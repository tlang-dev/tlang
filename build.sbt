name := "tlang"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.8-1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "3.3.3" % Test

libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.10"
libraryDependencies += "org.snakeyaml" % "snakeyaml-engine" % "2.2.1"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.20"

//antlr4 plugin
antlr4Version in Antlr4 := "4.8-1"
antlr4PackageName in Antlr4 := Some("dev.tlang.tlang")
enablePlugins(Antlr4Plugin)

//Code coverage
coverageMinimum := 80
coverageFailOnMinimum := false
coverageHighlighting := true
