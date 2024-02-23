package dev.tlang.tlang.runner

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc, HelperInternalFunc}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.loader.manifest.{Manifest, Stability}
import dev.tlang.tlang.loader.{Module, Resource}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core.Null

import scala.collection.immutable

class RunMainTest extends AnyFunSuite {

  test("Find main in helper") {
    val helper = HelperBlock(Null.empty(), Some(List(
      HelperFunc(Null.empty(), "func1", None, None, HelperContent(Null.empty(), None)),
      HelperFunc(Null.empty(), "main", None, None, HelperContent(Null.empty(), None)),
      HelperFunc(Null.empty(), "func2", None, None, HelperContent(Null.empty(), None)),
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
    val helper = HelperBlock(Null.empty(), Some(List(
      HelperFunc(Null.empty(), "main", None, None, HelperContent(Null.empty(), content)),
    )))
    val ast = DomainModel(Null.empty(), None, List(helper))
    val resource = Resource("Root", "", "", "Main", ast)
    val manifest = Manifest("Org", "Proj", "Prog", "1.0.0", Some(Stability.FINAL), 1, None, None)
    val module = Module("Root", manifest, immutable.Map("Main" -> resource), None, "Main")
    RunMain.runMainFile(module)()
    assert(mainFuncRan)
  }
}
