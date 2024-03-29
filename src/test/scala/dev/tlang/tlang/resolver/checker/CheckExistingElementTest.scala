package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.loader.Resource
import dev.tlang.tlang.resolver.NameAlreadyUsed
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class CheckExistingElementTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Check same func name in helper") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        | func myFunc() {
        | }
        |
        | func myFunc() {
        | }
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildAst.build(fakeContext, parser.domainModel())
    val res = CheckExistingElement.checkExistingElement(Resource("", "", "", "", impl)).swap.toOption.get
    assert("NameAlreadyUsed" == res.head.code)
    assert("myFunc" == res.head.asInstanceOf[NameAlreadyUsed].name)
  }

  test("Check same tmpl name") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[java] myTmpl {
        |}
        |
        |tmpl[scala] myTmpl {
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildAst.build(fakeContext, parser.domainModel())
    val res = CheckExistingElement.checkExistingElement(Resource("", "", "", "", impl)).swap.toOption.get
    assert("NameAlreadyUsed" == res.head.code)
    assert("myTmpl" == res.head.asInstanceOf[NameAlreadyUsed].name)
  }

  test("Check same name in model") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set myModel {}
        |let myModel = 5
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildAst.build(fakeContext, parser.domainModel())
    val res = CheckExistingElement.checkExistingElement(Resource("", "", "", "", impl)).swap.toOption.get
    assert("NameAlreadyUsed" == res.head.code)
    assert("myModel" == res.head.asInstanceOf[NameAlreadyUsed].name)
  }

  test("Check name in uses") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """use mySomething
        |use some.thing as mySomething
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildAst.build(fakeContext, parser.domainModel())
    val res = CheckExistingElement.checkExistingElement(Resource("", "", "", "", impl)).swap.toOption.get
    assert("NameAlreadyUsed" == res.head.code)
    assert("mySomething" == res.head.asInstanceOf[NameAlreadyUsed].name)
  }

  test("Check names inside func") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        | func myFunc() {
        |   let myVar=5
        |   let myVar="5"
        | }
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildAst.build(fakeContext, parser.domainModel())
    val res = CheckExistingElement.checkExistingElement(Resource("", "", "", "", impl)).swap.toOption.get
    assert("NameAlreadyUsed" == res.head.code)
    assert("myVar" == res.head.asInstanceOf[NameAlreadyUsed].name)
  }

  test("Check names inside func with func name") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        | func something() {
        |   let something=5
        | }
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildAst.build(fakeContext, parser.domainModel())
    val res = CheckExistingElement.checkExistingElement(Resource("", "", "", "", impl)).swap.toOption.get
    assert("NameAlreadyUsed" == res.head.code)
    assert("something" == res.head.asInstanceOf[NameAlreadyUsed].name)
  }


  test("Check func with tmpl") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        | func mySomething() {
        | }
        |}
        |tmpl[scala] mySomething {
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildAst.build(fakeContext, parser.domainModel())
    val res = CheckExistingElement.checkExistingElement(Resource("", "", "", "", impl)).swap.toOption.get
    assert("NameAlreadyUsed" == res.head.code)
    assert("mySomething" == res.head.asInstanceOf[NameAlreadyUsed].name)
  }

}
