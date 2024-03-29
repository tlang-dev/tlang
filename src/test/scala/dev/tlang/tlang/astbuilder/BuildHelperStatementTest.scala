package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.call.{CallArrayObject, CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{TLangLong, TLangString}
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildHelperStatementTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Call simple var") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |myVar
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    assert("myVar" == func.block.content.get.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Call simple array") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |myArray[1]
        |myArray2["one"]
        |myArray3[anyVar]
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val array1 = func.block.content.get.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallArrayObject]
    val array2 = func.block.content.get(1).asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallArrayObject]
    val array3 = func.block.content.get.last.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallArrayObject]
    assert("myArray" == array1.name)
    assert(1 == array1.position.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert("myArray2" == array2.name)
    assert("one" == array2.position.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("myArray3" == array3.name)
    assert("anyVar" == array3.position.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Call string") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |"myValue"
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    assert("myValue" == func.block.content.get.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Call int") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |1337
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    assert(1337 == func.block.content.get.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangLong].getElement)
  }

}
