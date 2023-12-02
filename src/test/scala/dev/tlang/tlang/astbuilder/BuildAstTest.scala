package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.helper.HelperBlock
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.ast.LangBlock
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildAstTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Test one expose") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |expose myFunc
        |helper {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val header = BuildAst.build(fakeContext, parser.domainModel()).header.get
    assert("myFunc".equals(header.exposes.get.head.name))
    assert(header.uses.isEmpty)
  }

  test("Test multiple exposes") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |expose myFunc1
        |expose myFunc2
        |expose myFunc3
        |helper {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val header = BuildAst.build(fakeContext, parser.domainModel()).header.get
    assert(3 == header.exposes.get.size)
    assert("myFunc1".equals(header.exposes.get.head.name))
    assert("myFunc2".equals(header.exposes.get(1).name))
    assert("myFunc3".equals(header.exposes.get.last.name))
    assert(header.uses.isEmpty)
  }

  test("Test one use") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |use MyPackage
        |helper {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val header = BuildAst.build(fakeContext, parser.domainModel()).header.get
    assert("MyPackage".equals(header.uses.get.head.parts.head))
    assert(1 == header.uses.get.head.parts.size)
    assert(header.exposes.isEmpty)
  }

  test("Test one use with two parts") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |use MyPackage.myFunc
        |helper {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val header = BuildAst.build(fakeContext, parser.domainModel()).header.get
    assert("MyPackage".equals(header.uses.get.head.parts.head))
    assert("myFunc".equals(header.uses.get.head.parts.last))
    assert(2 == header.uses.get.head.parts.size)
    assert(header.exposes.isEmpty)
  }

  test("Test multiple uses") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |use MyPackage.myFunc1
        |use MyPackage.myFunc2
        |use MyPackage2
        |helper {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val header = BuildAst.build(fakeContext, parser.domainModel()).header.get
    assert(3 == header.uses.get.size)
    assert("MyPackage".equals(header.uses.get.head.parts.head))
    assert("myFunc1".equals(header.uses.get.head.parts.last))
    assert("MyPackage".equals(header.uses.get(1).parts.head))
    assert("myFunc2".equals(header.uses.get(1).parts.last))
    assert("MyPackage2".equals(header.uses.get.last.parts.head))
    assert(1 == header.uses.get.last.parts.size)
    assert(header.exposes.isEmpty)
  }

  test("Test multiple exposes and uses") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |expose myFunc1
        |expose myFunc2
        |expose myFunc3
        |
        |use MyPackage.myFunc1
        |use MyPackage.myFunc2
        |use MyPackage2
        |helper {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val header = BuildAst.build(fakeContext, parser.domainModel()).header.get
    assert(3 == header.exposes.get.size)
    assert("myFunc1".equals(header.exposes.get.head.name))
    assert("myFunc2".equals(header.exposes.get(1).name))
    assert("myFunc3".equals(header.exposes.get.last.name))

    assert(3 == header.uses.get.size)
    assert("MyPackage".equals(header.uses.get.head.parts.head))
    assert("myFunc1".equals(header.uses.get.head.parts.last))
    assert("MyPackage".equals(header.uses.get(1).parts.head))
    assert("myFunc2".equals(header.uses.get(1).parts.last))
    assert("MyPackage2".equals(header.uses.get.last.parts.head))
    assert(1 == header.uses.get.last.parts.size)
  }

  test("Test domain blocks") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """
        |model {
        |}
        |helper {
        |}
        |tmpl[scala] myTmpl {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val blocks = BuildAst.build(fakeContext, parser.domainModel())
    assert(3 == blocks.body.size)
    assert(blocks.body.head.isInstanceOf[ModelBlock])
    assert(blocks.body(1).isInstanceOf[HelperBlock])
    assert(blocks.body.last.isInstanceOf[LangBlock])
  }
}
