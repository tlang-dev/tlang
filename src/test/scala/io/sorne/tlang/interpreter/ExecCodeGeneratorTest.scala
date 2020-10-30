package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCodeGeneratorTest extends AnyFunSuite {

  test("Test simple execution") {
    val context = Context(mutable.Map(), mutable.Map())
    val statement = new HelperStatement()
    ExecCodeGenerator.run(statement, context)
  }
}
