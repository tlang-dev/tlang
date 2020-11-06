package io.sorne.tlang.runner

import io.sorne.tlang.ast.helper.{HelperBlock, HelperContent, HelperFunc}
import io.sorne.tlang.loader.Module
import org.scalatest.funsuite.AnyFunSuite

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

}
