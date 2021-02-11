package dev.tlang.tlang.runner

import dev.tlang.tlang.ast.helper.{HelperBlock, HelperFunc}
import dev.tlang.tlang.interpreter.ExecFunc
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.loader.{BuildModuleTree, FileResourceLoader, Module, TBagManager}
import dev.tlang.tlang.resolver.ResolveContext

import java.io.File
import java.nio.file.Paths
import java.util.UUID.randomUUID

object RunMain {

  implicit val loader: FileResourceLoader.type = FileResourceLoader
  implicit val remoteLoader: RemoteLoader.type = RemoteLoader
  implicit val tBagManager: TBagManager.type = TBagManager

  def runDir(name: String): Unit = {
//    val newName = name.split(File.separator).mkString("/")
    val uuid = randomUUID().toString
    //BuildModuleTree.build(Paths.get(parts.slice(0, parts.size - 1).mkString(File.separator)), Some(parts.last)) match {
    BuildModuleTree.build(new File(name).toPath, None, uuid) match {
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
            ExecFunc.run(main, Context(List(main.scope)))
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
