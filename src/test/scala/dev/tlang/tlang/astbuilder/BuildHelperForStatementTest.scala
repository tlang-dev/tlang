package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.{TLangLong, TLangString}
import dev.tlang.tlang.ast.helper.{ForType, HelperFor}
import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.{TLangLong, TLangString}
import dev.tlang.tlang.ast.helper.{ForType, HelperFor}
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildHelperForStatementTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("For 1 to 10") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |for(i 1 to 10) {
        |"myBody"
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(1 == forStmt.start.get.asInstanceOf[TLangLong].getElement)
    assert(ForType.TO == forStmt.forType)
    assert(10 == forStmt.array.asInstanceOf[TLangLong].getElement)
    assert("myBody" == forStmt.body.content.get.head.asInstanceOf[TLangString].getElement)
  }

  test("For 0 until 10") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |for(i 0 until 10) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(0 == forStmt.start.get.asInstanceOf[TLangLong].getElement)
    assert(ForType.UNTIL == forStmt.forType)
    assert(10 == forStmt.array.asInstanceOf[TLangLong].getElement)
    assert(forStmt.body.content.isEmpty)
  }

  test("For in var") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |for(i in myVar) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(forStmt.start.isEmpty)
    assert(ForType.IN == forStmt.forType)
    assert("myVar" == forStmt.array.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(forStmt.body.content.isEmpty)
  }

}
