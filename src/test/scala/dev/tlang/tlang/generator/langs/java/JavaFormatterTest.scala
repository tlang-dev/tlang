package dev.tlang.tlang.generator.langs.java

import org.scalatest.funsuite.AnyFunSuite

class JavaFormatterTest extends AnyFunSuite {

//  val fakeContext: ContextResource = ContextResource("", "", "", "")

  val RET: String = System.lineSeparator()

  /*test("Format func in class") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |impl MyClass {
        | func myFunc(): String {
        |   callAFunc(one, two, three)
        | }
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res = Formatter.format(NewJavaGenerator.genBlock(impl), JavaFormatter.formatter())
    assert(
      "public class MyClass {" + RET + JavaFormatter.spaces + "public String myFunc() {" + RET + JavaFormatter.spaces + JavaFormatter.spaces + "callAFunc(one, two, three);" + RET + JavaFormatter.spaces + "}" + RET + RET + "}" + RET == res)
  }*/

 /* test("Format condition in class") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |impl MyClass {
        | func myFunc(): String {
        |   if(a == b && b >= c) {
        |   }
        | }
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res = Formatter.format(NewJavaGenerator.genBlock(impl), JavaFormatter.formatter())
    assert(
      "public class MyClass {" + RET + JavaFormatter.spaces + "public String myFunc() {" + RET + JavaFormatter.spaces + JavaFormatter.spaces + "if(a == b && b >= c) {" + RET + RET + JavaFormatter.spaces + JavaFormatter.spaces + "}"+ RET +RET + JavaFormatter.spaces + "}" + RET + RET + "}" + RET == res)
  }*/

}
