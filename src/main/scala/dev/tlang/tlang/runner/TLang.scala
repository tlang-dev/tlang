package dev.tlang.tlang.runner

import dev.tlang.tlang.bagman.{FileResourceWriter, Packager, ResourceWriter}
import dev.tlang.tlang.loader.{FileResourceLoader, ModuleLoader, ResourceLoader, TBagManager}

import java.io.File
import dev.tlang.tlang.lsp.LSPServer

import java.nio.file.Paths

object TLang {

  val TLANG_VERSION = "0.1"
  val FILE_EXTENSION = "tlang"

  def main(args: Array[String]): Unit = {
    val newArgs = args.toList.slice(1, args.length)
    if (args != null && args.length > 0) {
      args(0) match {
        case "run" => runDir(newArgs)
        case "lsp" => runLSP(newArgs)
        case "pack" => pack(newArgs)
        case "push" => push(newArgs)
        case "help" => help(newArgs)
        case "version" => version(newArgs)
      }
    }
  }

  def runDir(args: List[String]): Unit = {
    if (args.nonEmpty) {
      val path = new File(args.head)
      if (path.exists() && path.isFile && path.getName.endsWith("." + FILE_EXTENSION)) {

      } else if (path.isDirectory) {

      } else println("The path \"" + path.toString + "\" is either not a directory or not a .tlang file")
    } else println("Please provide a directory or a .tlang file")
  }

  def runLSP(args: List[String]): Unit = {
    LSPServer.startLSPServer(4242)
  }

  def pack(args: List[String]): Unit = {
    Packager.createDefaultPackage(Paths.get(args.head))
  }

  def push(args: List[String]): Unit = {

  }

  def help(args: List[String]): Unit = {
    println("Help")
  }

  def version(args: List[String]): Unit = {
    println("TLang version " + TLANG_VERSION)
  }

}
