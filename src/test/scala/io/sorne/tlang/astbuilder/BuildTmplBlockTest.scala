package io.sorne.tlang.astbuilder

import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildTmplBlockTest extends AnyFunSuite {

  test("Test use in TmplBloc") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl {
        |lang="scala"
        |use "io.sorne.tlang"
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    assert("io.sorne.tlang".equals(BuildTmplBlock.build(parser.tmplBlock().tmplUse().get(0)).name))
  }

  test("Test uses in TmplBloc") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl {
        |lang="scala"
        |use "io.sorne.tlang1"
        |use "io.sorne.tlang2"
        |use "io.sorne.tlang3"
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val uses = BuildTmplBlock.build(parser.tmplBlock().tmplUses)
    assert("io.sorne.tlang1".equals(uses.head.name))
    assert("io.sorne.tlang2".equals(uses(1).name))
    assert("io.sorne.tlang3".equals(uses.last.name))
  }

  test("Test uses in TmplBloc with empty list") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl {
        |lang="scala"
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val uses = BuildTmplBlock.build(parser.tmplBlock().tmplUses)
    assert(uses.isEmpty)
  }

  test("Test impl name") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl {
        |lang="scala"
        |impl test {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock().tmplImpl)
    assert("test".equals(impl.name))
    assert(impl.fors.isEmpty)
  }

  test("Test impl with for") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl {
        |lang="scala"
        |impl test for test1{
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock().tmplImpl)
    assert("test1".equals(impl.fors.get.head.name))
    assert(1 == impl.fors.get.size)
  }

  test("Test impl with fors") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl {
        |lang="scala"
        |impl test for test1, test2, test3 {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock().tmplImpl)
    assert("test1".equals(impl.fors.get.head.name))
    assert("test2".equals(impl.fors.get(1).name))
    assert("test3".equals(impl.fors.get.last.name))
    assert(3 == impl.fors.get.size)
  }

}
