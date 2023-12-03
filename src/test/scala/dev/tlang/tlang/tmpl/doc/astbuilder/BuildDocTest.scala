package dev.tlang.tlang.tmpl.doc.astbuilder

import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.doc.ast.{DocImg, DocPlainText, DocStruct, DocText}
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuiteLike

import scala.jdk.CollectionConverters._

class BuildDocTest extends AnyFunSuiteLike {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("testBuildDocLink") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[HtmlDoc] myTmpl() doc {
        |[link "https://tlang.dev" "TLang Dev"]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocLink(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocText().tmplDocLink())
    assert("https://tlang.dev".equals(impl.getElement.src))
    assert("TLang Dev".equals(impl.getElement.name))
  }

  test("testBuildCodeBlock") {
    val lexer = new CommonLexer(CharStreams.fromString(
      "tmpl[HtmlDoc] myTmpl() doc {" + "\n" +
        "[code \"scala\" \n" +
        "\"\"\"class MyClass {}\"\"\"" + "]\n" +
        "}".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildCodeBlock(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocText().tmplDocCodeBlock())
    assert("scala".equals(impl.getElement.lang))
    assert("class MyClass {}".equals(impl.getElement.code))
  }

  test("testBuildDocImg") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[HtmlDoc] myTmpl() doc {
        |[img "https://tlang.dev/logo.png" "TLang Dev Icon"]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocImg(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocText().tmplDocImg())
    assert("https://tlang.dev/logo.png".equals(impl.getElement.src))
    assert("TLang Dev Icon".equals(impl.getElement.alt.get))
  }

  test("testBuildDocList") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[HtmlDoc] myTmpl() doc {
        |[list "number"
        | * Some text
        | * # A title
        | * [img "https://tlang.dev/logo.png" "TLang Dev Icon"]
        |]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocList(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocText().tmplDocList())
    assert("number".equals(impl.getElement.order))
    assert("Some text".equals(impl.getElement.contents.head.contents.head.asInstanceOf[DocText].text.asInstanceOf[DocPlainText].text))
    assert("A title".equals(impl.getElement.contents(1).contents.head.asInstanceOf[DocStruct].title))
    assert("TLang Dev Icon".equals(impl.getElement.contents.last.contents.head.asInstanceOf[DocText].text.asInstanceOf[DocImg].alt.get))
  }

  test("testBuildDocText") {

  }

  test("testBuildDocSpan") {

  }

  test("testBuildAnyLevel") {

  }

  test("testBuildTmplDoc") {

  }

  test("testBuildDocSec") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[HtmlDoc] myTmpl() doc {
        |[section "section1"
        |This is the content
        |]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocSec(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocSec())
    assert("section1".equals(impl.getElement.title))
    assert("This is the content".equals(impl.getElement.content.contents.head.asInstanceOf[DocText].text.asInstanceOf[DocPlainText].text))
  }

  test("testBuildDocTable") {

  }

  test("testBuildDocInclude") {

  }

  test("testBuildDocStruct") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[HtmlDoc] myTmpl() doc {
        |# This is some text
        |This is the content
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocStruct(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocStruct())
    assert(impl.level == 1)
    assert("This is some text".equals(impl.getElement.title))
    assert("This is the content".equals(impl.getElement.content.get.contents.head.asInstanceOf[DocText].text.asInstanceOf[DocPlainText].text))
  }

  test("testBuildDocStruct Level 2") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[HtmlDoc] myTmpl() doc {
        |## This is some text
        |This is the content
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocStruct(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocStruct())
    assert(impl.level == 2)
    assert("This is some text".equals(impl.getElement.title))
    assert("This is the content".equals(impl.getElement.content.get.contents.head.asInstanceOf[DocText].text.asInstanceOf[DocPlainText].text))
  }

  test("testBuildDocPlainText") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[HtmlDoc] myTmpl() doc {
        |This is some text
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocPlainText(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocText().tmplDocPlainText())
    assert("This is some text".equals(impl.getElement.text))
  }

  test("testBuildAsIs") {
    val lexer = new CommonLexer(CharStreams.fromString(
      "tmpl[HtmlDoc] myTmpl() doc {" + "\n" +
        "[asis " +
        "\"\"\"<html><header></header></html>\"\"\"" + "]\n" +
        "}".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildDoc.buildDocAsIs(fakeContext, parser.tmplBlock().block.tmplDoc().tmplDocBlock().tmplDocContent().contents.asScala.toList.head.tmplDocAsIs())
    assert("<html><header></header></html>".equals(impl.getElement.content))
  }

}
