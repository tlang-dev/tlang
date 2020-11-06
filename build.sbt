name := "io.sorne.tlang"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.8-1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "3.3.3" % Test

//libraryDependencies += "com.lihaoyi" %% "upickle" % "1.2.2"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.10"
//libraryDependencies += "org.eclipse.lsp4j" % "org.eclipse.lsp4j" % "0.9.0"


antlr4Version in Antlr4 := "4.8-1"
antlr4PackageName in Antlr4 := Some("io.sorne.tlang")
enablePlugins(Antlr4Plugin)