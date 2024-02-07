package dev.tlang.tlang.tmpl.style.astbuilder

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallVarObject}
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.common.ast.TmplInterpretedID
import dev.tlang.tlang.tmpl.lang.ast.primitive.LangLongValue
import dev.tlang.tlang.tmpl.style.ast.{StyleArray, StyleInclude, StyleSetAttribute}
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike

class BuildStyleTest extends AnyFunSuiteLike {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("test simple style") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """style[css] myTmpl() {
        |h1 {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildStyle.buildStyleStruct(fakeContext, parser.tmplBlock().tmplStyle().styleBlocks().styleStruct(0))
    assert("h1".equals(impl.name.get.toString))
  }

  test("test params") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """style[css] myTmpl() {
        |h1 [test1, test2:test3, test4:5, <[test6()]>, ${test7}] {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildStyle.buildStyleStruct(fakeContext, parser.tmplBlock().tmplStyle().styleBlocks().styleStruct(0))
    val params = impl.params.get
    assert("h1".equals(impl.name.get.toString))
    assert("test1".equals(params.head.asInstanceOf[StyleSetAttribute].value.toString))
    assert("test2".equals(params(1).asInstanceOf[StyleSetAttribute].name.get.toString))
    assert("test3".equals(params(1).asInstanceOf[StyleSetAttribute].value.toString))
    assert("test4".equals(params(2).asInstanceOf[StyleSetAttribute].name.get.toString))
    assert(5 == params(2).asInstanceOf[StyleSetAttribute].value.asInstanceOf[LangLongValue].value)
    assert("test6".equals(params(3).asInstanceOf[StyleInclude].call.statement.statements.head.asInstanceOf[CallFuncObject].name.get))
    assert("test7".equals(params.last.asInstanceOf[StyleSetAttribute].value.asInstanceOf[TmplInterpretedID].call.statements.head.asInstanceOf[CallVarObject].name))
  }

  test("test attributes") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """style[css] myTmpl() {
        |h1 {
        |attr1,
        |attr2:attr3,
        |attr4:5,
        |<[include()]>,
        |attr6:"attr7",
        |attr8: [attr9, attr10, attr11],
        |${attr12}
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildStyle.buildStyleStruct(fakeContext, parser.tmplBlock().tmplStyle().styleBlocks().styleStruct(0))
    val attrs = impl.attrs.get
    assert("h1".equals(impl.name.get.toString))
    assert("attr1".equals(attrs.head.asInstanceOf[StyleSetAttribute].value.toString))
    assert("attr2".equals(attrs(1).asInstanceOf[StyleSetAttribute].name.get.toString))
    assert("attr3".equals(attrs(1).asInstanceOf[StyleSetAttribute].value.toString))
    assert("attr4".equals(attrs(2).asInstanceOf[StyleSetAttribute].name.get.toString))
    assert(5 == attrs(2).asInstanceOf[StyleSetAttribute].value.asInstanceOf[LangLongValue].value)
    assert("include".equals(attrs(3).asInstanceOf[StyleInclude].call.statement.statements.head.asInstanceOf[CallFuncObject].name.get))
    assert("attr6".equals(attrs(4).asInstanceOf[StyleSetAttribute].name.get.toString))
    assert("attr7".equals(attrs(4).asInstanceOf[StyleSetAttribute].value.toString))
    assert("attr8".equals(attrs(5).asInstanceOf[StyleSetAttribute].name.get.toString))
    assert("attr9".equals(attrs(5).asInstanceOf[StyleSetAttribute].value.asInstanceOf[StyleArray].values.head.asInstanceOf[StyleSetAttribute].value.toString))
    assert("attr10".equals(attrs(5).asInstanceOf[StyleSetAttribute].value.asInstanceOf[StyleArray].values(1).asInstanceOf[StyleSetAttribute].value.toString))
    assert("attr11".equals(attrs(5).asInstanceOf[StyleSetAttribute].value.asInstanceOf[StyleArray].values.last.asInstanceOf[StyleSetAttribute].value.toString))
    assert("attr12".equals(attrs.last.asInstanceOf[StyleSetAttribute].value.asInstanceOf[TmplInterpretedID].call.statements.head.asInstanceOf[CallVarObject].name))
  }

}
