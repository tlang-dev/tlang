package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{TLangLong, TLangString}
import dev.tlang.tlang.ast.helper.{ForType, HelperFor}
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildHelperForStatementTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("For 1 to 10") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |for(i 1 to 10) {
        |"myBody"
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(1 == forStmt.start.get.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(ForType.TO == forStmt.forType)
    assert(10 == forStmt.array.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert("myBody" == forStmt.body.content.get.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("For 0 until 10") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |for(i 0 until 10) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(0 == forStmt.start.get.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(ForType.UNTIL == forStmt.forType)
    assert(10 == forStmt.array.content.toOption.get.asInstanceOf[TLangLong].getElement)
    assert(forStmt.body.content.isEmpty)
  }

  test("For in var") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |for(i in myVar) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(forStmt.start.isEmpty)
    assert(ForType.IN == forStmt.forType)
    assert("myVar" == forStmt.array.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(forStmt.body.content.isEmpty)
  }

}
