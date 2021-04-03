package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.helper.HelperObjType
import dev.tlang.tlang.ast.tmpl.call.{TmplCallArray, TmplCallFunc, TmplCallObj, TmplCallVar}
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.primitive._
import dev.tlang.tlang.ast.tmpl.{TmplExprBlock, TmplIf, TmplMultiValue, TmplVar}
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters._

class BuildTmplBlockTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Template name") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |use dev.tlang.tlang
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val tmpl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    assert("myTmpl" == tmpl.name)
    assert("scala" == tmpl.lang)
    assert(tmpl.params.isEmpty)
  }

  test("Template parameters") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl (param1 String, param2 Bool) {
        |use dev.tlang.tlang
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val tmpl = BuildTmplBlock.build(fakeContext, parser.tmplBlock())
    assert("myTmpl" == tmpl.name)
    assert("param1" == tmpl.params.get.head.param.get)
    assert("String" == tmpl.params.get.head.`type`.asInstanceOf[HelperObjType].name)
    assert("param2" == tmpl.params.get.last.param.get)
    assert("Bool" == tmpl.params.get.last.`type`.asInstanceOf[HelperObjType].name)
  }

  test("Test use in TmplBloc") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |use dev.tlang.tlang
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    assert("dev.tlang.tlang" == BuildTmplBlock.buildUse(fakeContext, parser.tmplBlock().tmplUse().get(0)).parts.mkString("."))
  }

  test("Test uses in TmplBloc") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |use dev.tlang.tlang1
        |use dev.tlang.tlang2
        |use dev.tlang.tlang3
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val uses = BuildTmplBlock.buildUses(fakeContext, parser.tmplBlock().tmplUses.asScala.toList)
    assert("dev.tlang.tlang1" == uses.head.parts.mkString("."))
    assert("dev.tlang.tlang2" == uses(1).parts.mkString("."))
    assert("dev.tlang.tlang3" == uses.last.parts.mkString("."))
  }

  test("Test uses in TmplBloc with empty list") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val uses = BuildTmplBlock.buildUses(fakeContext, parser.tmplBlock().tmplUses.asScala.toList)
    assert(uses.isEmpty)
  }

  test("Test impl name") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("test" == impl.name.toString)
    assert(impl.fors.isEmpty)
  }

  test("Test impl with for") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test for test1{
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("test1" == impl.fors.get.head.name.toString)
    assert(1 == impl.fors.get.size)
  }

  test("Test impl with fors") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test for test1, test2, test3 {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("test1" == impl.fors.get.head.name.toString)
    assert("test2" == impl.fors.get(1).name.toString)
    assert("test3" == impl.fors.get.last.name.toString)
    assert(3 == impl.fors.get.size)
  }

  test("Simple var") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |var myVar :String = "myValue"
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val res = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplVar]
    assert("myVar" == res.name.toString)
    assert("String" == res.`type`.get.name.toString)
    assert("myValue" == res.value.get.content.toOption.get.asInstanceOf[TmplStringValue].value.toString)
  }

  test("Call var") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |myVar
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val res = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar]
    assert("myVar" == res.name.toString)
  }

  test("Call array") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |myArray[1]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val res = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallArray]
    assert("myArray" == res.name.toString)
    assert(1 == res.elem.content.toOption.get.asInstanceOf[TmplLongValue].value)
  }

  test("Call func") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |myFunc(1)("param2", hasName = true)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val res = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc]
    assert("myFunc" == res.name.toString)
    assert(1 == res.currying.get.head.params.get.head.value.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("param2" == res.currying.get.last.params.get.head.value.content.toOption.get.asInstanceOf[TmplStringValue].value.toString)
    assert("hasName" == res.currying.get.last.params.get.last.name.get.toString)
    assert(res.currying.get.last.params.get.last.value.content.toOption.get.asInstanceOf[TmplBoolValue].value)
  }

  test("Condition block") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |if(((true && myVar==myVar2) || myVar3 < myVar4)) {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val res = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplIf].cond
    val trueVal = res.content.left.toOption.get.content.left.toOption.get
    val andMyVar = res.content.left.toOption.get.content.left.toOption.get.next.get
    val eqMyVar2 = res.content.left.toOption.get.content.left.toOption.get.next.get._2.next.get
    val orMyVar3 = res.content.left.toOption.get.next.get
    val lessMyVar4 = res.content.left.toOption.get.next.get._2.next.get
    assert(trueVal.content.toOption.get.asInstanceOf[TmplBoolValue].value)
    assert("myVar" == andMyVar._2.content.toOption.get.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name.toString)
    assert(Operator.EQUAL == eqMyVar2._1)
    assert("myVar2" == eqMyVar2._2.content.toOption.get.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name.toString)
    assert(Operator.OR == orMyVar3._1)
    assert("myVar3" == orMyVar3._2.content.toOption.get.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name.toString)
    assert(Operator.LESSER == lessMyVar4._1)
    assert("myVar4" == lessMyVar4._2.content.toOption.get.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name.toString)
  }

  test("Multi value") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |(1, "value2", [1,2,3])
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val res = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplMultiValue]
    val array = res.values.last.asInstanceOf[TmplArrayValue]
    assert(1 == res.values.head.asInstanceOf[TmplLongValue].value)
    assert("value2" == res.values(1).asInstanceOf[TmplStringValue].value.toString)
    assert(1 == array.params.get.head.value.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(2 == array.params.get(1).value.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(3 == array.params.get.last.value.content.toOption.get.asInstanceOf[TmplLongValue].value)
  }

  test("Entity") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |new (1, param2 "value2") {
        |attr1 :Int[] [1,2,3]
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val res = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplEntityValue]
    val attr1 = res.attrs.get.head
    assert(1 == res.params.get.head.value.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("param2" == res.params.get.last.attr.get.toString)
    assert("value2" == res.params.get.last.value.content.toOption.get.asInstanceOf[TmplStringValue].value.toString)
    assert("attr1" == attr1.attr.get.toString)
    assert("Int" == attr1.`type`.get.name.toString)
    assert(attr1.`type`.get.isArray)
    assert(1 == attr1.value.content.toOption.get.asInstanceOf[TmplArrayValue].params.get.head.value.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(2 == attr1.value.content.toOption.get.asInstanceOf[TmplArrayValue].params.get(1).value.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(3 == attr1.value.content.toOption.get.asInstanceOf[TmplArrayValue].params.get.last.value.content.toOption.get.asInstanceOf[TmplLongValue].value)
  }

  test("If") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |if(1==1) callMyFunc()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val ifStmt = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplIf]
    val cond = ifStmt.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc" == ifStmt.content.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

  test("If with expression block") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |if(1==1) {
        |callMyFunc1()
        |callMyFunc2()
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val ifStmt = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplIf]
    val cond = ifStmt.cond
    val block = ifStmt.content.asInstanceOf[TmplExprBlock]
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc1" == block.exprs.head.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
    assert("callMyFunc2" == block.exprs.last.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

  test("If else") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |if(1==1) callMyFunc1() else callMyFunc2()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val ifStmt = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplIf]
    val cond = ifStmt.cond
    val elseBlock = ifStmt.elseBlock.get.left.toOption.get
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc1" == ifStmt.content.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
    assert("callMyFunc2" == elseBlock.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

  test("If else if") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |if(1==1) callMyFunc1() else if(2==2) callMyFunc2()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val ifStmt = BuildTmplBlock.build(fakeContext, parser.tmplBlock()).content.get.head.asInstanceOf[TmplIf]
    val cond = ifStmt.cond
    val elseIf = ifStmt.elseBlock.get.toOption.get
    val cond2 = elseIf.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc1" == ifStmt.content.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
    assert(Operator.EQUAL == cond2.next.get._1)
    assert(2 == cond2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert(2 == cond2.next.get._2.content.toOption.get.asInstanceOf[TmplLongValue].value)
    assert("callMyFunc2" == elseIf.content.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

}
