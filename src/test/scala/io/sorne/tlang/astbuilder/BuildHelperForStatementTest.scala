package io.sorne.tlang.astbuilder

import io.sorne.tlang.ast.common.call.{CallObject, CallVarObject}
import io.sorne.tlang.ast.helper.{ForType, HelperFor, HelperIf}
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildHelperForStatementTest extends AnyFunSuite{

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
    val func = BuildHelperBlock.build(parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(1 == forStmt.start.get.statements.head.asInstanceOf[HelperNewInt].value)
    assert(ForType.TO == forStmt.forType)
    assert(10 == forStmt.array.statements.head.asInstanceOf[HelperNewInt].value)
    assert("\"myBody\"" == forStmt.body.content.get.head.asInstanceOf[CallObject].statements.head.asInstanceOf[HelperNewString].value)
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
    val func = BuildHelperBlock.build(parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(0 == forStmt.start.get.statements.head.asInstanceOf[HelperNewInt].value)
    assert(ForType.UNTIL == forStmt.forType)
    assert(10 == forStmt.array.statements.head.asInstanceOf[HelperNewInt].value)
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
    val func = BuildHelperBlock.build(parser.helperBlock()).funcs.get.head
    val forStmt = func.block.content.get.head.asInstanceOf[HelperFor]
    assert("i" == forStmt.variable)
    assert(forStmt.start.isEmpty)
    assert(ForType.IN == forStmt.forType)
    assert("myVar" == forStmt.array.statements.head.asInstanceOf[CallVarObject].name)
    assert(forStmt.body.content.isEmpty)
  }

}
