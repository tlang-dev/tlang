package dev.tlang.tlang.generator.langs.scalalang

import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.astbuilder.tmpl.BuildTmplBlock
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters._

class ScalaImplFuncGeneratorTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

 /* test("Test func to scala") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test {
        |func test {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    assert(ScalaImplFuncGenerator.gen(func).contains("def test {"))
  }

  test("Test func to scala with ()") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test {
        |func test() {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    assert(ScalaImplFuncGenerator.gen(func).contains("def test() {"))
  }

  test("Test func to scala with one parameter") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test {
        |func test(myParam: MyType) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    assert(ScalaImplFuncGenerator.gen(func).contains("def test(myParam: MyType) {"))
  }

  test("Test func to scala with one array parameter") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test {
        |func test(myParam: MyType[]) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    assert(ScalaImplFuncGenerator.gen(func).contains("def test(myParam: Array[MyType]) {"))
  }

  test("Test func to scala with parameters and generics") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test {
        |func test(myParam: MyType<JustAType<AlsoGeneric>>, mySecondParam: MySecondType<SomeThing, AnotherThing<EvenSomethingElse>>) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    assert(ScalaImplFuncGenerator.gen(func).contains("def test(myParam: MyType[JustAType[AlsoGeneric]], mySecondParam: MySecondType[SomeThing, AnotherThing[EvenSomethingElse]]) {"))
  }

  test("Test func to scala with currying") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test {
        |func test(myParam: MyType[])(mySecondParam: MySecondType<JustTrying>) {
        |}
        |}}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    val func = impl.content.get.head.asInstanceOf[TmplFunc]
    assert(ScalaImplFuncGenerator.gen(func).contains("def test(myParam: Array[MyType])(mySecondParam: MySecondType[JustTrying]) {"))
  }*/

}
