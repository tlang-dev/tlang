package dev.tlang.tlang.loader.remote

import org.scalatest.funsuite.AnyFunSuite

class RepoManifestLoaderTest extends AnyFunSuite {

  test("Parse repo manifest") {
    val yaml =
      """
        |name: MyProgram
        |fullName: MyOrg/MyProject/MyProgram 1.2.3:beta:4
        |hash: hash12345
        |file: myProgram.tbag
        |""".stripMargin
    val manifest = RepoManifestLoader.parseManifest(yaml)
    assert("MyProgram" == manifest.name)
    assert("MyOrg/MyProject/MyProgram 1.2.3:beta:4" == manifest.fullName)
    assert("hash12345" == manifest.hash)
    assert("myProgram.tbag" == manifest.file)
  }

}
