package io.sorne.tlang.loader

import java.io.{FileNotFoundException, IOException}
import java.nio.file.Paths

import scala.io.{BufferedSource, Source}

object FileResourceLoader extends ResourceLoader {

  def load(root: String, fromRoot: String, pkg: String, name: String): Either[LoaderError, String] = {
    val rsc = Paths.get(root, fromRoot, pkg, name).toString
    var buffer: BufferedSource = null
    try {
      buffer = Source.fromFile(rsc)
      Right(buffer.getLines().mkString("\n"))
    } catch {
      case _: FileNotFoundException => Left(LoaderError("FILE_NOT_FOUND", "File:" + rsc))
      case e: IOException => Left(LoaderError("CANNOT_READ", "File:" + rsc + "\n" + e.getMessage))
    } finally {
      if (buffer != null) buffer.close()
    }
  }

}
