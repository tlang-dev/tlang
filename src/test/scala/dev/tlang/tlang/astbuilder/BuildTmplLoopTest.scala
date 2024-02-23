package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.tmpl.lang.ast.LangExprBlock
import dev.tlang.tlang.tmpl.lang.ast.call.{LangCallFunc, LangCallObj}
import dev.tlang.tlang.tmpl.lang.ast.loop.{LangDoWhile, LangWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive.LangLongValue
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core
import tlang.internal.ContextResource

class BuildTmplLoopTest extends AnyFunSuite {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String(""), new core.String(""))


  test("While with expression") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |while(1==1) callMyFunc()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val loop = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangWhile]
    val cond = loop.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc" == loop.content.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

  test("While with expression block") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |while(1==1) {
        | callMyFunc1()
        | callMyFunc2()
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val loop = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangWhile]
    val cond = loop.cond
    val block = loop.content.asInstanceOf[LangExprBlock]
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc1" == block.exprs.head.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
    assert("callMyFunc2" == block.exprs.last.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

  test("Do while with expression") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |do callMyFunc()
        |while(1==1)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val loop = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangDoWhile]
    val cond = loop.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc" == loop.content.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

  test("Do while with expression block") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |do {
        | callMyFunc1()
        | callMyFunc2()
        |} while(1==1)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val loop = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangDoWhile]
    val cond = loop.cond
    val block = loop.content.asInstanceOf[LangExprBlock]
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc1" == block.exprs.head.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
    assert("callMyFunc2" == block.exprs.last.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

}
