package io.sorne.tlang.loader.manifest

import org.scalatest.funsuite.AnyFunSuite

class ManifestLoaderTest extends AnyFunSuite {

  test("Parse manifest") {
    val yaml =
      """name: MyProgram
        |project: MyProject
        |organisation: MyOrganisation
        |version: 1.33.7
        |stability: final
        |releaseNumber: 2
        |dependencies:
        |  - TLang/IO/File 1.0.0:alpha:2 file
        |  - TLang/Generator/Generator 1.2.0:beta:3 generator
        |""".stripMargin
    val manifest = ManifestLoader.parseManifest(yaml)
    assert("MyProgram" == manifest.name)
    assert("MyProject" == manifest.project)
    assert("MyOrganisation" == manifest.organisation)
    assert("1.33.7" == manifest.version)
    assert(Stability.FINAL == manifest.stability.get)
    assert(2 == manifest.releaseNumber)
    assert(Dependency("TLang", "IO", "File", "1.0.0", Stability.ALPHA, 2, "file") == manifest.dependencies.get.head)
    assert(Dependency("TLang", "Generator", "Generator", "1.2.0", Stability.BETA, 3, "generator") == manifest.dependencies.get.last)
  }

  test("Map to manifest with all") {
    val manifest = ManifestLoader.mapToManifest(Map(
      "name" -> "MyProgram",
      "project" -> "MyProject",
      "organisation" -> "MyOrganisation",
      "version" -> "1.33.7",
      "stability" -> "final",
      "releaseNumber" -> 2.toInt,
    ))
    assert("MyProgram" == manifest.name)
    assert("MyProject" == manifest.project)
    assert("MyOrganisation" == manifest.organisation)
    assert("1.33.7" == manifest.version)
    assert(Stability.FINAL == manifest.stability.get)
    assert(2 == manifest.releaseNumber)
    assert(manifest.dependencies.isEmpty)
  }

  test("Map to manifest with default values") {
    val manifest = ManifestLoader.mapToManifest(Map())
    assert("" == manifest.name)
    assert("" == manifest.project)
    assert("" == manifest.organisation)
    assert("" == manifest.version)
    assert(manifest.stability.isEmpty)
    assert(1 == manifest.releaseNumber)
  }

  test("Get stability") {
    assert(Stability.FINAL == ManifestLoader.extractStability(Map("stability" -> "final")).get)
    assert(Stability.RC == ManifestLoader.extractStability(Map("stability" -> "rc")).get)
    assert(Stability.BETA == ManifestLoader.extractStability(Map("stability" -> "beta")).get)
    assert(Stability.ALPHA == ManifestLoader.extractStability(Map("stability" -> "alpha")).get)
    assert(ManifestLoader.extractStability(Map("stability" -> "anything")).isEmpty)
    assert(ManifestLoader.extractStability(Map()).isEmpty)
  }

  test("Get dependency") {
    val dependency = ManifestLoader.getDependency("MyOrganisation/MyProject/MyProgram 1.33.7:alpha:2 myProgram")
    assert("MyProgram" == dependency.name)
    assert("MyProject" == dependency.project)
    assert("MyOrganisation" == dependency.organisation)
    assert("1.33.7" == dependency.version)
    assert(Stability.ALPHA == dependency.stability)
    assert(2 == dependency.releaseNumber)
    assert("myProgram" == dependency.alias)
  }

}
