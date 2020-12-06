name := "io.sorne.tlang"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.antlr" % "antlr4-runtime" % "4.8-1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "3.3.3" % Test

libraryDependencies += "org.json4s" %% "json4s-native" % "3.6.10"
libraryDependencies += "org.snakeyaml" % "snakeyaml-engine" % "2.2.1"

val monocleVersion = "2.1.0"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % "test"
)

//antlr4 plugin
antlr4Version in Antlr4 := "4.8-1"
antlr4PackageName in Antlr4 := Some("io.sorne.tlang")
enablePlugins(Antlr4Plugin)

//Code coverage
coverageMinimum := 80
coverageFailOnMinimum := false
coverageHighlighting := true
