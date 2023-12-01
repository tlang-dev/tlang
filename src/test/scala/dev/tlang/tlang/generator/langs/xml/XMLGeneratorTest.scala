package dev.tlang.tlang.generator.langs.xml

import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
import dev.tlang.tlang.{TLangLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class XMLGeneratorTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Simple xml document") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |new XMLDoc(
        |param1 "one",
        |param2 true
        |)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res =  XMLGenerator.genBlock(impl).toString
    assert("""<XMLDoc param1="one" param2="true"/>""" == res)
  }

  test("Simple xml document with children") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |new XMLDoc(
        |param1 "one",
        |param2 true
        |){
        |new ChildOne(),
        |new ChildTwo(childParam1 1, childParam2 "two")
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res =  XMLGenerator.genBlock(impl).toString
    assert("""<XMLDoc param1="one" param2="true"><ChildOne/><ChildTwo childParam1="1" childParam2="two"/></XMLDoc>""" == res)
  }


  test("XML header tag") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |
        |new `?xml`(
        |version "1.0",
        |encoding "UTF-8"
        |)
        |
        |new XMLDoc(
        |param1 "one",
        |param2 true
        |)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res =  XMLGenerator.genBlock(impl).toString
    assert("""<?xml version="1.0" encoding="UTF-8" ?><XMLDoc param1="one" param2="true"/>""" == res)
  }

}
