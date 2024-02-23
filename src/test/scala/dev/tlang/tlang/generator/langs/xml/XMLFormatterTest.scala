package dev.tlang.tlang.generator.langs.xml

import org.scalatest.funsuite.AnyFunSuite

class XMLFormatterTest extends AnyFunSuite {

//  val fakeContext: ContextResource = ContextResource("", "", "", "")

  val RET: String = System.lineSeparator()

/*  test("Simple formatting with children") {
    val lexer = new CommonLexer(CharStreams.fromString(
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
    val res = Formatter.format(XMLGenerator.genBlock(impl), XMLFormatter.formatter())
    assert(s"""<XMLDoc param1="one" param2="true">$RET${XMLFormatter.spaces}<ChildOne />$RET${XMLFormatter.spaces}<ChildTwo childParam1="1" childParam2="two" />$RET</XMLDoc>$RET""" == res)
  }*/
}
