package io.sorne.tlang.generator.java

import io.sorne.tlang.ast.tmpl.{TmplImpl, TmplProp}
import io.sorne.tlang.astbuilder.BuildTmplBlock
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class JavaGeneratorTest extends AnyFunSuite {

  test("Package") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |pkg my.package
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock())
    assert(new JavaGenerator().generate(impl).contains("package my.package;"))
  }

  test("Uses") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |use my.package1
        |use my.package2
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.build(parser.tmplBlock())
    assert(new JavaGenerator().generate(impl).contains("import my.package1;"))
    assert(new JavaGenerator().generate(impl).contains("import my.package2;"))
  }

  test("Simple class") {
    val impl = TmplImpl(None, None, "MyClass", None, None)
    val res = JavaGenerator.genImpl(impl)
    assert(res.contains("public class MyClass {"))
  }

  test("Simple interface") {
    val impl = TmplImpl(None, Some(TmplProp(List("public", "interface"))), "MyInterface", None, None)
    val res = JavaGenerator.genImpl(impl)
    assert(res.contains("public interface MyInterface {"))
  }

}
