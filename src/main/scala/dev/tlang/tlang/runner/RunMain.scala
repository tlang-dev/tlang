package dev.tlang.tlang.runner

import dev.tlang.tlang.ast.helper.{HelperBlock, HelperFunc}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.interpreter.{ExecError, ExecFunc}
import dev.tlang.tlang.loader.remote.RemoteLoader
import dev.tlang.tlang.loader.{BuildModuleTree, FileResourceLoader, Module, TBagManager}
import dev.tlang.tlang.resolver.ResolveContext
import tlang.core.{Null, Value}

import java.io.File
import java.util.UUID.randomUUID
import scala.collection.mutable

object RunMain {

  implicit val loader: FileResourceLoader.type = FileResourceLoader
  implicit val remoteLoader: RemoteLoader.type = RemoteLoader
  implicit val tBagManager: TBagManager.type = TBagManager

  def runDir(name: String): Unit = {
    //    val newName = name.split(File.separator).mkString("/")
    val uuid = randomUUID().toString
    //BuildModuleTree.build(Paths.get(parts.slice(0, parts.size - 1).mkString(File.separator)), Some(parts.last)) match {
    BuildModuleTree.build(new File(name).toPath, uuid) match {
      case Left(error) => println("Error while loading the program (" + error.code + "): " + error.message)
      case Right(module) => runMainFile(module)()
    }
  }

  def runTBag(): Unit = {

  }

  def runMainFile(module: Module)(args: Option[Value[_]] = None): Either[ExecError, Option[List[Value[_]]]] = {
    module.resources.get(module.mainFile) match {
      case Some(resource) =>
        val helpers = resource.ast.body.filter(_.isInstanceOf[HelperBlock]).map(_.asInstanceOf[HelperBlock])
        var func: Option[HelperFunc] = None
        helpers.foreach(block => {
          findMainInHelper(block).foreach(f => func = Some(f))
        })
        func match {
          case Some(main) =>
            ResolveContext.resolveContext(module) match {
              case Left(errors) =>
                errors.foreach(error => println(error.code + ">> " + error.message))
                Left(new ExecError("RESOLVER_ERROR", "Error while resolving the module", Null.empty()))
              case Right(_) => ExecFunc.run(main, Context(List(main.scope, Scope(variables = if (args.isDefined) mutable.Map("args" -> args.get) else mutable.Map()))))
            }
          case None =>
            println("No main func found in Main file")
            Left(new ExecError("NO_MAIN_FUNC", "No main func found in Main file", Null.empty()))
        }
      case None => println("Non main file found in " + module.rootDir)
        Left(new ExecError("NO_MAIN_FILE", "No main file found", Null.empty()))
    }
  }

  def findMainInHelper(helper: HelperBlock): Option[HelperFunc] = {
    if (helper.funcs.isNotNull.get()) {
      helper.funcs.get().getRecords.find(_.name == "main")
    } else {
      None
    }
  }

}
