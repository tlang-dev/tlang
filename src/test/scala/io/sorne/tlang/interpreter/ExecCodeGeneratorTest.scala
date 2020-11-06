package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCodeGeneratorTest extends AnyFunSuite {

  test("Test simple execution") {
    val context = Context(List(Scope(mutable.Map(), mutable.Map())))
    val statement = new HelperStatement()
    ExecCodeGenerator.run(statement, context)
  }
}
