package io.sorne.tlang.runner

import java.io.File
import java.nio.file.Paths

import io.sorne.tlang.ast.helper.{HelperBlock, HelperFunc}
import io.sorne.tlang.interpreter.ExecFunc
import io.sorne.tlang.interpreter.context.Context
import io.sorne.tlang.loader.{BuildModuleTree, FileResourceLoader, Module}
import io.sorne.tlang.resolver.ResolveContext

object RunMain {

  implicit val loader: FileResourceLoader.type = FileResourceLoader

  def runFile(name: String): Unit = {
    val parts = name.split(File.separator)
    BuildModuleTree.build(Paths.get(parts.slice(0, parts.size - 1).mkString(File.separator)), Some(parts.last)) match {
      case Left(error) => println("Error while loading the program (" + error.code + "): " + error.message)
      case Right(module) => runMainFile(module)
    }
  }

  def runTBag(): Unit = {

  }

  def runMainFile(module: Module): Unit = {
    module.resources.get(module.mainFile) match {
      case Some(resource) =>
        val helpers = resource.ast.body.filter(_.isInstanceOf[HelperBlock]).map(_.asInstanceOf[HelperBlock])
        var func: Option[HelperFunc] = None
        helpers.foreach(block => {
          findMainInHelper(block).foreach(f => func = Some(f))
        })
        func match {
          case Some(main) =>
            ResolveContext.resolveContext(module)
            ExecFunc.run(main, Context(List()))
          case None =>
        }
      case None =>
    }
  }

  def findMainInHelper(helper: HelperBlock): Option[HelperFunc] = {
    helper.funcs match {
      case Some(func) => func.find(_.name == "main")
      case None => None
    }
  }
}
