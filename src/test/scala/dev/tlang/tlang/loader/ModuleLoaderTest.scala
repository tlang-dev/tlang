package dev.tlang.tlang.loader

import dev.tlang.tlang.loader.manifest.Stability
import dev.tlang.tlang.loader.manifest.{Dependency, Stability}
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.{Path, Paths}

class ModuleLoaderTest extends AnyFunSuite {

  test("Search local repo") {
   implicit val manager: TBagManager = new TBagManager {
      override def extract(tarGzFile: Path, dest: Path): Either[LoaderError, Unit] = Right(())

      override def isDirectory(path: Path): Boolean = true

      override def findTBagFile(path: Path): Option[File] = Some(new File("/path/to/my/file.tbag"))

     override def compress(dir: Path, dest: Path): Unit = ???
   }
    val dep = Dependency("MyOrg", "MyProject", "MyProgram", "1.2.3", Stability.ALPHA, 0)
    val path = ModuleLoader.searchLocalRepo(dep).toOption.get.get
    assert(Paths.get("/path", "to", "my", "file.tbag") == path)
  }

  test("Search remote repo") {

  }

}
