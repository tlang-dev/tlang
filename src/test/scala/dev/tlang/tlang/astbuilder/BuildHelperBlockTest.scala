package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.{ArrayType, ObjType}
import dev.tlang.tlang.ast.helper.HelperFuncType
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildHelperBlockTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Simple empty func") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    assert("myFunc".equals(func.name))
    assert(func.currying.isEmpty)
    assert(func.block.content.isEmpty)
  }

  test("Simple empty func with ()") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc() {
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    assert("myFunc".equals(func.name))
    assert(func.currying.isEmpty)
    assert(func.block.content.isEmpty)
  }

  test("One set of parameters") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc(param1: String, param2: Int[], param3: () => Bool) {
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val curry = func.currying.get.head
    val funcType = curry.params.last.`type`.asInstanceOf[HelperFuncType]
    assert("myFunc".equals(func.name))
    assert("param1".equals(curry.params.head.param.get))
    assert("String".equals(curry.params.head.`type`.asInstanceOf[ObjType].name))
    assert("param2".equals(curry.params(1).param.get))
    assert("Int".equals(curry.params(1).`type`.asInstanceOf[ArrayType].name))
    assert("param3".equals(curry.params.last.param.get))
    assert(funcType.params.isEmpty)
    assert("Bool".equals(funcType.returns.get.head.asInstanceOf[ObjType].name))
  }

  test("With returned values declared") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc : String, Int[] {
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val returns = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head.returns.get
    assert("String".equals(returns.head.asInstanceOf[ObjType].name))
    assert("Int".equals(returns.last.asInstanceOf[ArrayType].name))
  }

  test("With params and returned values") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc(param1: String, param2: Int[], param3: (Int[], String) => Bool):  String, Int[], (String) => Int[] {
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val curry = func.currying.get.head
    val funcType = curry.params.last.`type`.asInstanceOf[HelperFuncType]
    assert("myFunc".equals(func.name))
    assert("param1".equals(curry.params.head.param.get))
    assert("String".equals(curry.params.head.`type`.asInstanceOf[ObjType].name))
    assert("param2".equals(curry.params(1).param.get))
    assert("Int".equals(curry.params(1).`type`.asInstanceOf[ArrayType].name))
    assert("param3".equals(curry.params.last.param.get))
    assert("Int".equals(funcType.params.get.head.params.head.`type`.asInstanceOf[ArrayType].name))
    assert("String".equals(funcType.params.get.head.params.last.`type`.asInstanceOf[ObjType].name))
    assert("Bool".equals(funcType.returns.get.head.asInstanceOf[ObjType].name))

    val returns = func.returns.get
    val retFuncType = func.returns.get.last.asInstanceOf[HelperFuncType]
    assert("String".equals(returns.head.asInstanceOf[ObjType].name))
    assert("Int".equals(returns(1).asInstanceOf[ArrayType].name))
    assert("String".equals(retFuncType.params.get.head.params.head.`type`.asInstanceOf[ObjType].name))
    assert("Int".equals(retFuncType.returns.get.head.asInstanceOf[ArrayType].name))
  }

  test("Currying") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc(param1: String, param2: Int[])(param3: () => Bool, param4: String) {
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val firstCurry = func.currying.get.head
    val secondCurry = func.currying.get.last
    val funcType = secondCurry.params.head.`type`.asInstanceOf[HelperFuncType]
    assert("myFunc".equals(func.name))
    assert("param1".equals(firstCurry.params.head.param.get))
    assert("String".equals(firstCurry.params.head.`type`.asInstanceOf[ObjType].name))
    assert("param2".equals(firstCurry.params(1).param.get))
    assert("Int".equals(firstCurry.params(1).`type`.asInstanceOf[ArrayType].name))
    assert("param3".equals(secondCurry.params.head.param.get))
    assert(funcType.params.isEmpty)
    assert("Bool".equals(funcType.returns.get.head.asInstanceOf[ObjType].name))
    assert("String".equals(secondCurry.params.last.`type`.asInstanceOf[ObjType].name))
  }


}
