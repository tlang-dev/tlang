package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.tmpl.lang.ast.func.TmplFunc
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.ast.TmplStringID
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters._

class BuildTmplFuncTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Test build func") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |func func1 {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplLang().tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("func1".equals(impl.content.get.head.asInstanceOf[TmplFunc].name.toString))
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
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplLang().tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("func1".equals(impl.content.get.head.asInstanceOf[TmplFunc].name.toString))
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
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplLang().tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param = func.curries.get.head.params.get.head
    assert("func1".equals(func.name.toString))
    assert("myParam" == param.name.asInstanceOf[TmplStringID].id)
    assert("MyType" == param.`type`.get.name.toString)
    assert(!param.`type`.get.isArray)
    assert(param.`type`.get.generic.isEmpty)
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
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplLang().tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param = func.curries.get.head.params.get.head
    assert("func1" == func.name.toString)
    assert("myParam" == param.name.asInstanceOf[TmplStringID].id)
    assert("MyType" == param.`type`.get.name.toString)
    assert(param.`type`.get.isArray)
    assert(param.`type`.get.generic.isEmpty)
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
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplLang().tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param = func.curries.get.head.params.get.head
    assert("func1" == func.name.toString)
    assert("myParam" == param.name.asInstanceOf[TmplStringID].id)
    assert("MyType" == param.`type`.get.name.toString)
    assert("AnotherType" == param.`type`.get.generic.head.types.head.name.toString)
    assert("YetAnotherType" == param.`type`.get.generic.head.types.last.name.toString)
    assert("AndSoOn" == param.`type`.get.generic.head.types.last.generic.get.types.head.name.toString)
    assert(!param.`type`.get.isArray)
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
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplLang().tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    val param1 = func.curries.get.head.params.get.head
    val param2 = func.curries.get.last.params.get.head

    assert("func1" == func.name.toString)

    assert("myParam" == param1.name.asInstanceOf[TmplStringID].id)
    assert("MyType" == param1.`type`.get.name.toString)
    assert(!param1.`type`.get.isArray)
    assert(param1.`type`.get.generic.isEmpty)

    assert("myParam2" == param2.name.asInstanceOf[TmplStringID].id)
    assert("MyType2" == param2.`type`.get.name.toString)
    assert(param2.`type`.get.isArray)
    assert(param2.`type`.get.generic.isEmpty)
  }

}
