package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.helper.HelperIf
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildHelperIfStatementTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Simple if with one var") {
    val lexer = new TLangLexer(CharStreams.fromString(
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
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val trueStmt = ifStmt.ifTrue
    val elseStmt = ifStmt.ifFalse
    assert("myVar" == ifStmt.condition.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ifStmt.condition.next.isEmpty)
    assert("callAnyVar" == trueStmt.get.content.get.head.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("callAnotherVar" == elseStmt.get.content.get.head.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Equal statement") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar == myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == ifStmt.condition.next.get._1)
    assert(ifStmt.ifTrue.isEmpty)
    assert(ifStmt.ifFalse.isEmpty)
  }

  test("Not equal statement") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar != myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.NOT_EQUAL == ifStmt.condition.next.get._1)
  }

  test("Greater statement") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar > myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.GREATER == ifStmt.condition.next.get._1)
  }

  test("Lesser statement") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar < myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.LESSER == ifStmt.condition.next.get._1)
  }

  test("Greater or equal statement") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar >= myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.GREATER_OR_EQUAL == ifStmt.condition.next.get._1)
  }

  test("Lesser or equal statement") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar <= myVar2) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    assert("myVar" == ifStmt.condition.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.next.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.LESSER_OR_EQUAL == ifStmt.condition.next.get._1)
  }

  test("AND and OR links") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar && myVar2 || myVar3) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val cond1 = ifStmt.condition
    val cond2 = cond1.next.get
    val cond3 = cond2
    val cond4 = cond3._2
    assert("myVar" == cond1.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == cond2._1)
    assert("myVar2" == cond2._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == cond3._1)
    assert("myVar3" == cond3._2.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("AND and OR plus conditions") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(myVar == myVar2 && myVar3 != myVar4 || myVar5 >= myVar6) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val cond1 = ifStmt.condition
    val cond2 = cond1.next.get
    val cond3 = cond1.next
    val cond4 = cond3.get._2.next
    val cond5 = cond3.get._2.next
    val cond6 = cond5.get._2.next
    assert("myVar" == cond1.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == cond2._1)
    assert("myVar2" == cond2._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == cond3.get._1)
    assert("myVar3" == cond3.get._2.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.NOT_EQUAL == cond4.get._1)
    assert("myVar4" == cond4.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == cond5.get._1)
    assert("myVar5" == cond5.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.GREATER_OR_EQUAL == cond6.get._1)
    assert("myVar6" == cond6.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Two conditions with parentheses") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if((myVar == myVar2) && (myVar3 != myVar4)) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val cond1 = ifStmt.condition
    val cond2 = ifStmt.condition.next
    val cond3 = ifStmt.condition.next
    val cond4 = cond3.get
    assert("myVar" == cond1.content.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == cond2.get._1)
    assert("myVar2" == cond2.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == cond3.get._1)
    assert("myVar3" == cond3.get._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.NOT_EQUAL == cond4._1)
    assert("myVar4" == cond4._2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("One condition plus two variables with parentheses") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if((myVar == myVar2 && myVar3) || myVar4) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val cond1 = ifStmt.condition.content.swap.toOption.get
    val cond2 = cond1.next.get
    val cond3 = cond2._2.next.get
    val cond4 = cond1.next.get
    assert("myVar" == cond1.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == cond2._1)
    assert("myVar2" == cond2._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == cond3._1)
    assert("myVar3" == cond3._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == cond4._1)
    assert("myVar4" == cond4._2.content.toOption.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

  test("Sub block with parentheses") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """helper {
        |func myFunc {
        |if(((myVar == myVar2 && myVar3) || myVar4) && myVar5) {
        |}
        |}
        |}
        |""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val func = BuildHelperBlock.build(fakeContext, parser.helperBlock()).funcs.get.head
    val ifStmt = func.block.content.get.head.asInstanceOf[HelperIf]
    val myVar = ifStmt.condition.content.left.toOption.get.content.toOption.get
    val equals = ifStmt.condition.content.left.toOption.get.next.get._1
    val myVar2 = ifStmt.condition.content.left.toOption.get.next.get._2.content.swap.toOption.get
    val and = ifStmt.condition.content.left.toOption.get.next.get._2.next.get._1
    val myVar3 = ifStmt.condition.content.left.toOption.get.next.get._2.next.get._2.content.swap.toOption.get
    val or = ifStmt.condition.content.left.toOption.get.next.get._1
    val myVar4 = ifStmt.condition.content.left.toOption.get.next.get._2
    val and2 = ifStmt.condition.next.get._1
    val myVar5 = ifStmt.condition.next.get._2.content.swap.toOption.get
    assert("myVar" == myVar.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.EQUAL == equals)
    assert("myVar2" == myVar2.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == and)
    assert("myVar3" == myVar3.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.OR == or)
    assert("myVar4" == myVar4.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(Operator.AND == and2)
    assert("myVar5" == myVar5.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

}
