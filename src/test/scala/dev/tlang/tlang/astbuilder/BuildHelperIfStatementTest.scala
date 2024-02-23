package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.helper.HelperIf
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core
import tlang.internal.ContextResource

class BuildHelperIfStatementTest extends AnyFunSuite {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String(""), new core.String(""))

  test("Simple if with one var") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar) {
        |callAnyVar
        |} else {
        |callAnotherVar
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val trueStmt = ifStmt.ifTrue
    val elseStmt = ifStmt.ifFalse
    assert("myVar" == ifStmt.condition.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ifStmt.condition.next.isEmpty)
    assert("callAnyVar" == trueStmt.get.content.get.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("callAnotherVar" == elseStmt.get.content.get.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Equal statement") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar == myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == ifStmt.condition.next.get._1)
    assert(ifStmt.ifTrue.isEmpty)
    assert(ifStmt.ifFalse.isEmpty)
  }

  test("Not equal statement") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar != myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.NOT_EQUAL == ifStmt.condition.next.get._1)
  }

  test("Greater statement") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar > myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.GREATER == ifStmt.condition.next.get._1)
  }

  test("Lesser statement") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar < myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.LESSER == ifStmt.condition.next.get._1)
  }

  test("Greater or equal statement") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar >= myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.GREATER_OR_EQUAL == ifStmt.condition.next.get._1)
  }

  test("Lesser or equal statement") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar <= myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[Operation].content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.LESSER_OR_EQUAL == ifStmt.condition.next.get._1)
  }

  test("AND and OR links") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar && myVar2 || myVar3) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val cond1 = ifStmt.condition
    val cond2 = cond1.next.get
    val cond3 = cond2._2.next.get
    assert("myVar" == cond1.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == cond2._1)
    assert("myVar2" == cond2._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == cond3._1)
    assert("myVar3" == cond3._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("AND and OR plus conditions") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar == myVar2 && myVar3 != myVar4 || myVar5 >= myVar6) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val cond1 = ifStmt.condition
    val equalVar2 = cond1.next.get
    val andVar3 = equalVar2._2.next.get
    val notVar4 = andVar3._2.next.get
    val orVar5 = notVar4._2.next.get
    val beVar6 = orVar5._2.next.get
    assert("myVar" == cond1.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == equalVar2._1)
    assert("myVar2" == equalVar2._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == andVar3._1)
    assert("myVar3" == andVar3._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.NOT_EQUAL == notVar4._1)
    assert("myVar4" == notVar4._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == orVar5._1)
    assert("myVar5" == orVar5._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.GREATER_OR_EQUAL == beVar6._1)
    assert("myVar6" == beVar6._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Two conditions with parentheses") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if((myVar == myVar2) && (myVar3 != myVar4)) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val op1 = ifStmt.condition
    val op2 = ifStmt.condition.next.get
    val myVar = op1.content.swap.toOption.get
    val eqVar2 = myVar.next.get
    val myVar3 = op2._2.content
    val notVar4 = op2._2.next.get
    assert("myVar" == myVar.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == eqVar2._1)
    assert("myVar2" == eqVar2._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == op2._1)
    assert("myVar3" == myVar3.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.NOT_EQUAL == notVar4._1)
    assert("myVar4" == notVar4._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("One condition plus two variables with parentheses") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if((myVar == myVar2 && myVar3) || myVar4) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val cond1 = ifStmt.condition.content.swap.toOption.get
    val cond2 = cond1.next.get
    val cond3 = cond2._2.next.get
    val cond4 = ifStmt.condition.next.get
    assert("myVar" == cond1.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == cond2._1)
    assert("myVar2" == cond2._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == cond3._1)
    assert("myVar3" == cond3._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == cond4._1)
    assert("myVar4" == cond4._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Sub block with parentheses") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(((myVar == myVar2 && myVar3) || myVar4) && myVar5) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val myVar = ifStmt.condition.content.left.toOption.get.content.swap.toOption.get
    val equals = myVar.next.get._1
    val myVar2 = myVar.next.get
    val and = myVar2._2.next.get._1
    val myVar3 = myVar2._2.next.get
    val or = ifStmt.condition.content.left.toOption.get.next.get._1
    val myVar4 = ifStmt.condition.content.left.toOption.get.next.get._2
    val and2 = ifStmt.condition.next.get._1
    val myVar5 = ifStmt.condition.next.get._2.content.toOption.get
    assert("myVar" == myVar.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == equals)
    assert("myVar2" == myVar2._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == and)
    assert("myVar3" == myVar3._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == or)
    assert("myVar4" == myVar4.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == and2)
    assert("myVar5" == myVar5.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

}
