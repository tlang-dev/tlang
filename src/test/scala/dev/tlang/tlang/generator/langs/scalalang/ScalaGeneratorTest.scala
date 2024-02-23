package dev.tlang.tlang.generator.langs.scalalang

import org.scalatest.funsuite.AnyFunSuite

class ScalaGeneratorTest extends AnyFunSuite {

//  val fakeContext: ContextResource = ContextResource("", "", "", "")

  /*test("Test impl for") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test for Test1{
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert(ScalaGenerator.genImpl(impl).contains("extends Test1"))
  }

  test("Test impl fors") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl Test for Test1, Test2, Test3{
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert(ScalaGenerator.genImpl(impl).contains("extends Test1 with Test2, Test3"))
  }*/

//  test("Generate call func") {
//    val lexer = new CommonLexer(CharStreams.fromString(
//      """tmpl[scala] myTmpl {
//        |myVar.myFunc(1.0, "two")(param3=true)
//        |}""".stripMargin))
//    val tokens = new CommonTokenStream(lexer)
//    val parser = new TLang(tokens)
//    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
//    val res = new ScalaGenerator().generate(impl)
//    assert(res.contains("myVar.myFunc(1.0, \"two\")(param3 = true)"))
//  }

 /* test("Generate call array") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |myVar.myArray[5]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    val res = new ScalaGenerator().generate(impl)
    assert(res.contains("myVar.myArray(5)"))
  }*/

}
