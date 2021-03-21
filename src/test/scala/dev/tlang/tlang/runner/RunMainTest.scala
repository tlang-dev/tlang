package dev.tlang.tlang.runner

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc, HelperInternalFunc}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.loader.manifest.{Manifest, Stability}
import dev.tlang.tlang.loader.{Module, Resource}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable

class RunMainTest extends AnyFunSuite {

  test("Find main in helper") {
    val helper = HelperBlock(None, Some(List(
      HelperFunc(None, "func1", None, None, HelperContent(None, None)),
      HelperFunc(None, "main", None, None, HelperContent(None, None)),
      HelperFunc(None, "func2", None, None, HelperContent(None, None)),
    )))

    val func = RunMain.findMainInHelper(helper).get
    assert("main" == func.name)
  }

  test("Run main file") {
    var mainFuncRan = false
    val content = Some(List(HelperInternalFunc((_: Context) => {
      mainFuncRan = true
      Right(None)
    })))
    val helper = HelperBlock(None, Some(List(
      HelperFunc(None, "main", None, None, HelperContent(None, content)),
    )))
    val ast = DomainModel(None, None, List(helper))
    val resource = Resource("Root", "", "", "Main", ast)
    val manifest = Manifest("Org", "Proj", "Prog", "1.0.0", Some(Stability.FINAL), 1, None)
    val module = Module("Root", manifest, immutable.Map("Main" -> resource), None, "Main")
    RunMain.runMainFile(module)
    assert(mainFuncRan)
  }
}
