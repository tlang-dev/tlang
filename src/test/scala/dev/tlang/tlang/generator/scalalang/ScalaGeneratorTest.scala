package dev.tlang.tlang.generator.scalalang

import dev.tlang.tlang.astbuilder.BuildTmplBlock
import io.sorne.tlang.{TLangLexer, TLangParser}
import io.sorne.tlang.astbuilder.BuildTmplBlock
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters._

class ScalaGeneratorTest extends AnyFunSuite {

  test("Test impl for") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test for Test1{
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert(ScalaGenerator.genImpl(impl).contains("extends Test1"))
  }

  test("Test impl fors") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test for Test1, Test2, Test3{
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert(ScalaGenerator.genImpl(impl).contains("extends Test1 with Test2, Test3"))
  }

  test("Generate call func") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |myVar.myFunc(1.0, "two")(param3=true)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock())
    val res = new ScalaGenerator().generate(impl)
    assert(res.contains("myVar.myFunc(1.0, \"two\")(param3 = true)"))
  }

  test("Generate call array") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |myVar.myArray[5]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock())
    val res = new ScalaGenerator().generate(impl)
    assert(res.contains("myVar.myArray(5)"))
  }

}
