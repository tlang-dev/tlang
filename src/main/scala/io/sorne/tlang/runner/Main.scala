package io.sorne.tlang.runner

import java.io.File

object Main {

  val FILE_EXTENSION = "tlang"

  def main(args: Array[String]): Unit = {
    if (args != null && args.length > 0) {
      args(0) match {
        case "run" =>
      }
    }
  }

  def run(args: List[String]): Unit = {
    if (args.nonEmpty) {
      val path = new File(args.head)
      if (path.exists() && path.isFile && path.getName.endsWith("." + FILE_EXTENSION)) {

      } else if (path.isDirectory) {

      } else println("The path \"" + path.toString + "\" is either not a directory or not a .tlang file")
    } else println("Please provide a directory or a .tlang file")
  }

  def runLSP(args: List[String]): Unit = {

  }

}
