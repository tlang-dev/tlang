package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType, HelperIf}
import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType, HelperIf}
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
    assert("myVar" == ifStmt.condition.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ifStmt.condition.link.isEmpty)
    assert(ifStmt.condition.nextBlock.isEmpty)
    assert(ifStmt.condition.content.toOption.get.condition.isEmpty)
    assert(ifStmt.condition.content.toOption.get.statement2.isEmpty)
    assert(ifStmt.condition.content.toOption.get.link.isEmpty)
    assert(ifStmt.condition.content.toOption.get.nextBlock.isEmpty)
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
    assert("myVar" == ifStmt.condition.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.EQUAL == ifStmt.condition.content.toOption.get.condition.get)
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
    assert("myVar" == ifStmt.condition.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.NOT_EQUAL == ifStmt.condition.content.toOption.get.condition.get)
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
    assert("myVar" == ifStmt.condition.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.GREATER == ifStmt.condition.content.toOption.get.condition.get)
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
    assert("myVar" == ifStmt.condition.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.LESSER == ifStmt.condition.content.toOption.get.condition.get)
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
    assert("myVar" == ifStmt.condition.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.GREATER_OR_EQUAL == ifStmt.condition.content.toOption.get.condition.get)
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
    assert("myVar" == ifStmt.condition.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert("myVar2" == ifStmt.condition.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.LESSER_OR_EQUAL == ifStmt.condition.content.toOption.get.condition.get)
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
    val cond1 = ifStmt.condition.content.toOption.get
    val cond2 = cond1.nextBlock.get
    val cond3 = cond2.content.toOption.get.nextBlock.get
    assert("myVar" == cond1.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.AND == cond1.link.get)
    assert("myVar2" == cond2.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.OR == cond2.content.toOption.get.link.get)
    assert("myVar3" == cond3.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
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
    val cond1 = ifStmt.condition.content.toOption.get
    val cond2 = cond1.nextBlock.get
    val cond3 = cond2.content.toOption.get.nextBlock.get
    assert("myVar" == cond1.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.EQUAL == cond1.condition.get)
    assert("myVar2" == cond1.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.AND == cond1.link.get)
    assert("myVar3" == cond2.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.NOT_EQUAL == cond2.content.toOption.get.condition.get)
    assert("myVar4" == cond2.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.OR == cond2.content.toOption.get.link.get)
    assert("myVar5" == cond3.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.GREATER_OR_EQUAL == cond3.content.toOption.get.condition.get)
    assert("myVar6" == cond3.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
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
    val cond1 = ifStmt.condition.content
    val cond2 = ifStmt.condition.nextBlock.get
    assert("myVar" == cond1.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.EQUAL == cond1.toOption.get.condition.get)
    assert("myVar2" == cond1.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.AND == ifStmt.condition.link.get)
    assert("myVar3" == cond2.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.NOT_EQUAL == cond2.content.toOption.get.condition.get)
    assert("myVar4" == cond2.content.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
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
    val cond1 = ifStmt.condition.content
    assert("myVar" == cond1.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.EQUAL == cond1.toOption.get.condition.get)
    assert("myVar2" == cond1.toOption.get.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.AND == cond1.toOption.get.link.get)
    assert("myVar3" == cond1.toOption.get.nextBlock.get.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.OR == ifStmt.condition.link.get)
    assert("myVar4" == ifStmt.condition.nextBlock.get.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
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
    val block1 = ifStmt.condition.content.left.toOption.get
    val block2 = block1.content.toOption.get
    val block3 = block2.nextBlock.get
    val block4 = block1.nextBlock.get
    val block5 = ifStmt.condition.nextBlock.get
    assert("myVar" == block2.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionType.EQUAL == block2.condition.get)
    assert("myVar2" == block2.statement2.get.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.AND == block2.link.get)
    assert("myVar3" == block3.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.OR == block1.link.get)
    assert("myVar4" == block4.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
    assert(ConditionLink.AND == ifStmt.condition.link.get)
    assert("myVar5" == block5.content.toOption.get.statement1.asInstanceOf[CallObject].statements.head.asInstanceOf[CallVarObject].name)
  }

}
