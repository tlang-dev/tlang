package dev.tlang.tlang.generator.langs.java

import dev.tlang.tlang.astbuilder.context.ContextResource
import org.scalatest.funsuite.AnyFunSuite

class NewJavaGeneratorTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

 /* test("Package") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |pkg my.package
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    assert("package my.package;" == NewJavaGenerator.genBlock(impl).toString)
  }

  test("Uses") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |use my.package1
        |use my.package2
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    assert("import my.package1;import my.package2;" == NewJavaGenerator.genBlock(impl).toString)
  }

  test("Simple class") {
    val impl = TmplImpl(None, None, None, TmplStringID(None, "MyClass"), None, None)
    val res = NewJavaGenerator.genImpl(impl)
    assert("public class MyClass{}" == res.toString)
  }

  test("Simple interface") {
    val impl = TmplImpl(None, None, Some(TmplProp(None, List(TmplStringID(None, "public"), TmplStringID(None, "interface")))), TmplStringID(None, "MyInterface"), None, None)
    val res = NewJavaGenerator.genContent(impl)
    assert("public interface MyInterface{}" == res.toString)
  }*/

}
