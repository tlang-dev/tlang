package dev.tlang.tlang.bagman

import dev.tlang.tlang.loader.manifest.{Manifest, Stability}
import dev.tlang.tlang.loader.{LoaderError, ResourceLoader, TBagManager}
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.{Path, Paths}

class PackagerTest extends AnyFunSuite {

  test("Gen simple PkgManifest") {
    val manifest = Manifest("name", "proj", "org", "1.0.0", Some(Stability.ALPHA), 1, None, None)
    val res = Packager.genManifest(manifest).toOption.get
    assert("org/proj/name/1.0.0/alpha/1" == res.path)
    assert("name.tbag" == res.files.head.name)
    assert("tbag" == res.files.head.fileType)
    assert("https://house.tlang.dev/org/proj/name/1.0.0/alpha/1/name.tbag" == res.files.head.remote.get.toString)
  }

  test("Create package") {
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

    implicit val resourceLoader: ResourceLoader = (_: String, _: String, _: String, _: String) => Right(yaml)

    var res: String = ""

    implicit val resourceWriter: ResourceWriter = (_: Path, _: String, content: String) => {
      res = content
    }

    implicit val manager: TBagManager = new TBagManager {
      override def extract(tarGzFile: Path, dest: Path): Either[LoaderError, Unit] = Right(())

      override def isDirectory(path: Path): Boolean = false

      override def findTBagFile(path: Path): Option[File] = None

      override def compress(dir: Path, dest: Path): Unit = ()
    }

    val tbagManifest =
      """path: MyOrganisation/MyProject/MyProgram/1.33.7/final/2
        |files:
        |  - name: MyProgram.tbag
        |    fileType: tbag
        |    checksum:
        |    remote: https://house.tlang.dev/MyOrganisation/MyProject/MyProgram/1.33.7/final/2/MyProgram.tbag
        |    local: """.stripMargin

    Packager.createPackage(Paths.get(""), Paths.get(""))

    assert(tbagManifest == res)
  }

}
