package io.sorne.tlang.loader

import java.io.{FileNotFoundException, IOException}
import java.nio.file.Paths

import scala.io.Source

object FileResourceLoader extends ResourceLoader {

  def load(root: String, fromRoot: String, pkg: String, name: String): Either[LoaderError, String] = {
    val rsc = Paths.get(root, fromRoot, pkg, name, "tlang").toString
    val buffer = Source.fromFile(rsc)
    try {
      Right(buffer.getLines.mkString)
    } catch {
      case _: FileNotFoundException => Left(LoaderError("FILE_NOT_FOUND", "File:" + rsc))
      case e: IOException => Left(LoaderError("CANNOT_READ", "File:" + rsc + "\n" + e.getMessage))
    } finally {
      buffer.close()
    }
  }

}
