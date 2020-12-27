package io.sorne.tlang.astbuilder

import io.sorne.tlang.ast.tmpl.func.TmplFunc
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite
import scala.jdk.CollectionConverters._

class BuildTmplFuncTest extends AnyFunSuite {

  test("Test build func") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |func func1 {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("func1".equals(impl.content.get.head.asInstanceOf[TmplFunc].name))
  }

  test("Test build func with ()") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |func func1() {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("func1".equals(impl.content.get.head.asInstanceOf[TmplFunc].name))
  }

  test("Test build func with one parameter") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |func func1(myParam: MyType) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param = func.curries.get.head.params.get.head
    assert("func1".equals(func.name))
    assert("myParam".equals(param.name))
    assert("MyType".equals(param.`type`.name))
    assert(!param.`type`.isArray)
    assert(param.`type`.generic.isEmpty)
  }

  test("Test build func with one array parameter") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |func func1(myParam: MyType[]) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param = func.curries.get.head.params.get.head
    assert("func1".equals(func.name))
    assert("myParam".equals(param.name))
    assert("MyType".equals(param.`type`.name))
    assert(param.`type`.isArray)
    assert(param.`type`.generic.isEmpty)
  }

  test("Test build func with one generic parameter") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |func func1(myParam: MyType<AnotherType, YetAnotherType<AndSoOn>>) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param = func.curries.get.head.params.get.head
    assert("func1".equals(func.name))
    assert("myParam".equals(param.name))
    assert("MyType".equals(param.`type`.name))
    assert("AnotherType".equals(param.`type`.generic.head.types.head.name))
    assert("YetAnotherType".equals(param.`type`.generic.head.types.last.name))
    assert("AndSoOn".equals(param.`type`.generic.head.types.last.generic.get.types.head.name))
    assert(!param.`type`.isArray)
  }

  test("Test build func with currying") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |func func1(myParam: MyType)(myParam2: MyType2[]) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param1 = func.curries.get.head.params.get.head
    val param2 = func.curries.get.last.params.get.head

    assert("func1".equals(func.name))

    assert("myParam".equals(param1.name))
    assert("MyType".equals(param1.`type`.name))
    assert(!param1.`type`.isArray)
    assert(param1.`type`.generic.isEmpty)

    assert("myParam2".equals(param2.name))
    assert("MyType2".equals(param2.`type`.name))
    assert(param2.`type`.isArray)
    assert(param2.`type`.generic.isEmpty)
  }

}
