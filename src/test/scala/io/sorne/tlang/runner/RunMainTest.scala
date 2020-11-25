package io.sorne.tlang.runner

import io.sorne.tlang.ast.DomainModel
import io.sorne.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc, HelperInternalFunc}
import io.sorne.tlang.interpreter.context.Context
import io.sorne.tlang.loader.{Module, Resource}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable

class RunMainTest extends AnyFunSuite {

  test("Find main in helper") {
    val helper = HelperBlock(Some(List(
      HelperFunc("func1", None, None, HelperContent(None)),
      HelperFunc("main", None, None, HelperContent(None)),
      HelperFunc("func2", None, None, HelperContent(None)),
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
    val helper = HelperBlock(Some(List(
      HelperFunc("main", None, None, HelperContent(content)),
    )))
    val ast = DomainModel(None, List(helper))
    val resource = Resource("Root", "", "", "Main", ast)
    val module = Module("Root", immutable.Map("Main" -> resource), None, "Main")
    RunMain.runMainFile(module)
    assert(mainFuncRan)
  }
}
