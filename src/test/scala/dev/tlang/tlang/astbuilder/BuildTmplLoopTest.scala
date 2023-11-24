package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.tmpl.lang.ast.call.{TmplCallFunc, TmplCallObj}
import dev.tlang.tlang.tmpl.lang.ast.loop.{TmplDoWhile, TmplWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive.TmplLongValue
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.ast.TmplExprBlock
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildTmplLoopTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("While with expression") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |while(1==1) callMyFunc()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val loop = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplWhile]
    val cond = loop.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc" == loop.content.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

  test("While with expression block") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |while(1==1) {
        | callMyFunc1()
        | callMyFunc2()
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val loop = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplWhile]
    val cond = loop.cond
    val block = loop.content.asInstanceOf[TmplExprBlock]
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc1" == block.exprs.head.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
    assert("callMyFunc2" == block.exprs.last.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

  test("Do while with expression") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |do callMyFunc()
        |while(1==1)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val loop = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplDoWhile]
    val cond = loop.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc" == loop.content.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

  test("Do while with expression block") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |do {
        | callMyFunc1()
        | callMyFunc2()
        |} while(1==1)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val loop = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplDoWhile]
    val cond = loop.cond
    val block = loop.content.asInstanceOf[TmplExprBlock]
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc1" == block.exprs.head.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
    assert("callMyFunc2" == block.exprs.last.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

}
