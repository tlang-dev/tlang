package dev.tlang.tlang.generator.formatter

import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
//import dev.tlang.tlang.generator.langs.java.{JavaFormatter, NewJavaGenerator}
import dev.tlang.tlang.{TLangLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class FormatterTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  val RET: String = System.lineSeparator()

 /* test("Simple formatting") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |pkg my.package
        |use my.package1
        |use my.package2
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res = Formatter.format(NewJavaGenerator.genBlock(impl), JavaFormatter.formatter())
    assert(
      """package my.package;
        |
        |import my.package1;
        |import my.package2;
        |""".stripMargin == res)
  }

  test("Format class") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |impl MyClass {
        |
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res = Formatter.format(NewJavaGenerator.genBlock(impl), JavaFormatter.formatter())
    assert("public class MyClass {" + RET + RET + "}" + RET == res)
  }

  test("Format func in class") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |impl MyClass {
        | func myFunc(): String {
        | }
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res = Formatter.format(NewJavaGenerator.genBlock(impl), JavaFormatter.formatter())
    assert(
      "public class MyClass {" + RET + JavaFormatter.spaces + "public String myFunc() {" + RET + RET + JavaFormatter.spaces + "}" + RET + RET + "}" + RET == res)
  }*/

}
