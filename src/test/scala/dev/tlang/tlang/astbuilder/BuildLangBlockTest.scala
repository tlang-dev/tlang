
package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.ast.call.{LangCallArray, LangCallFunc, LangCallObj, LangCallVar}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core
import tlang.internal.ContextResource

class BuildLangBlockTest extends AnyFunSuite {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String(""), new core.String(""))

  test("Template name") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl lang {
        |use dev.tlang.tlang
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val tmpl = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang())
    assert("myTmpl" == tmpl.name)
    assert("scala" == tmpl.langs.getValue.getValue)

    assert(tmpl.params.isEmpty)
  }

  test("Template parameters") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl (param1: String, param2: Bool) lang{
        |use dev.tlang.tlang
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val tmpl = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang())
    assert("myTmpl" == tmpl.name)
    assert("param1" == tmpl.params.get.head.statement.param.get)
    assert("String" == tmpl.params.get.head.statement.`type`.asInstanceOf[ObjType].name)
    assert("param2" == tmpl.params.get.last.statement.param.get)
    assert("Bool" == tmpl.params.get.last.statement.`type`.asInstanceOf[ObjType].name)
  }

  /*test("Test use in TmplBloc") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |use dev.tlang.tlang
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    assert("dev.tlang.tlang" == BuildTmplBlock.buildUse(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplUse().get(0)).parts.mkString("."))
  }

  test("Test uses in TmplBloc") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |use dev.tlang.tlang1
        |use dev.tlang.tlang2
        |use dev.tlang.tlang3
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val uses = BuildTmplBlock.buildUses(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplUses.asScala.toList)
    assert("dev.tlang.tlang1" == uses.head.parts.mkString("."))
    assert("dev.tlang.tlang2" == uses(1).parts.mkString("."))
    assert("dev.tlang.tlang3" == uses.last.parts.mkString("."))
  }

  test("Test uses in TmplBloc with empty list") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val uses = BuildTmplBlock.buildUses(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplUses.asScala.toList)
    assert(uses.isEmpty)
  }

  test("Test impl name") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("test" == impl.name.toString)
    assert(impl.fors.isEmpty)
  }

  test("Test impl with for") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test for test1{
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("test1" == impl.fors.get.types.head.name.toString)
    assert(1 == impl.fors.get.types.size)
  }

  test("Test impl with fors") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """tmpl[scala] myTmpl {
        |impl test for test1, test2, test3 {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val impl = BuildTmplBlock.buildImpl(fakeContext, parser.tmplBlock().block.tmplFullBlock().tmplContents.asScala.toList.head.tmplImpl())
    assert("test1" == impl.fors.get.types.head.name.toString)
    assert("test2" == impl.fors.get.types(1).name.toString)
    assert("test3" == impl.fors.get.types.last.name.toString)
    assert(3 == impl.fors.get.types.size)
  }*/

  test("Simple var") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl lang() {
        |var myVar :String = "myValue"
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val res = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangVar]
    assert("myVar" == res.name.toString)
    assert("String" == res.`type`.get.name.toString)
    assert("myValue" == res.value.get.content.toOption.get.asInstanceOf[LangStringValue].value.toString)
  }

  test("Call var") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |myVar
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val res = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar]
    assert("myVar" == res.name.toString)
  }

  test("Call array") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |myArray[1]
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val res = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallArray]
    assert("myArray" == res.name.toString)
    assert(1 == res.elem.content.toOption.get.asInstanceOf[LangLongValue].value)
  }

  test("Call func") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |myFunc(1)("param2", hasName : true)
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val res = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc]
    assert("myFunc" == res.name.toString)
    assert(1 == res.currying.get.head.params.get.head.asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("param2" == res.currying.get.last.params.get.head.asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangStringValue].value.toString)
    assert("hasName" == res.currying.get.last.params.get.last.asInstanceOf[LangSetAttribute].name.get.toString)
    assert(res.currying.get.last.params.get.last.asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangBoolValue].value)
  }

  test("Condition block") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |if(((true && myVar==myVar2) || myVar3 < myVar4)) {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val res = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangIf].cond
    val trueVal = res.content.left.toOption.get.content.left.toOption.get
    val andMyVar = res.content.left.toOption.get.content.left.toOption.get.next.get
    val eqMyVar2 = res.content.left.toOption.get.content.left.toOption.get.next.get._2.next.get
    val orMyVar3 = res.content.left.toOption.get.next.get
    val lessMyVar4 = res.content.left.toOption.get.next.get._2.next.get
    assert(trueVal.content.toOption.get.asInstanceOf[LangBoolValue].value)
    assert("myVar" == andMyVar._2.content.toOption.get.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar].name.toString)
    assert(Operator.EQUAL == eqMyVar2._1)
    assert("myVar2" == eqMyVar2._2.content.toOption.get.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar].name.toString)
    assert(Operator.OR == orMyVar3._1)
    assert("myVar3" == orMyVar3._2.content.toOption.get.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar].name.toString)
    assert(Operator.LESSER == lessMyVar4._1)
    assert("myVar4" == lessMyVar4._2.content.toOption.get.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar].name.toString)
  }

  test("Multi value") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |(1, "value2", [1,2,3])
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val res = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangMultiValue]
    val array = res.values.last.asInstanceOf[LangArrayValue]
    assert(1 == res.values.head.asInstanceOf[LangLongValue].value)
    assert("value2" == res.values(1).asInstanceOf[LangStringValue].value.toString)
    assert(1 == array.params.get.head.asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(2 == array.params.get(1).asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(3 == array.params.get.last.asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
  }

  test("Entity") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |new (1, param2 "value2") {
        |attr1 :Int[] [1,2,3]
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val res = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangEntityValue]
    val attr1 = res.attrs.get.head
    assert(1 == res.params.get.head.asInstanceOf[LangAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("param2" == res.params.get.last.asInstanceOf[LangAttribute].attr.get.toString)
    assert("value2" == res.params.get.last.asInstanceOf[LangAttribute].value.content.toOption.get.asInstanceOf[LangStringValue].value.toString)
    assert("attr1" == attr1.asInstanceOf[LangAttribute].attr.get.toString)
    assert("Int" == attr1.asInstanceOf[LangAttribute].`type`.get.name.toString)
    assert(attr1.asInstanceOf[LangAttribute].`type`.get.isArray)
    assert(1 == attr1.asInstanceOf[LangAttribute].value.content.toOption.get.asInstanceOf[LangArrayValue].params.get.head.asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(2 == attr1.asInstanceOf[LangAttribute].value.content.toOption.get.asInstanceOf[LangArrayValue].params.get(1).asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(3 == attr1.asInstanceOf[LangAttribute].value.content.toOption.get.asInstanceOf[LangArrayValue].params.get.last.asInstanceOf[LangSetAttribute].value.content.toOption.get.asInstanceOf[LangLongValue].value)
  }

  test("If") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |if(1==1) callMyFunc()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val ifStmt = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangIf]
    val cond = ifStmt.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc" == ifStmt.content.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

  test("If with expression block") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |if(1==1) {
        |callMyFunc1()
        |callMyFunc2()
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val ifStmt = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangIf]
    val cond = ifStmt.cond
    val block = ifStmt.content.asInstanceOf[LangExprBlock]
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc1" == block.exprs.head.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
    assert("callMyFunc2" == block.exprs.last.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

  test("If else") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |if(1==1) callMyFunc1() else callMyFunc2()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val ifStmt = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangIf]
    val cond = ifStmt.cond
    val elseBlock = ifStmt.elseBlock.get.left.toOption.get
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc1" == ifStmt.content.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
    assert("callMyFunc2" == elseBlock.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

  test("If else if") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """lang[scala] myTmpl() {
        |if(1==1) callMyFunc1() else if(2==2) callMyFunc2()
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val ifStmt = BuildTmplBlock.buildLangBlock(fakeContext, parser.tmplBlock().tmplLang()).content.content.get.head.asInstanceOf[LangIf]
    val cond = ifStmt.cond
    val elseIf = ifStmt.elseBlock.get.toOption.get
    val cond2 = elseIf.cond
    assert(Operator.EQUAL == cond.next.get._1)
    assert(1 == cond.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(1 == cond.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc1" == ifStmt.content.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
    assert(Operator.EQUAL == cond2.next.get._1)
    assert(2 == cond2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert(2 == cond2.next.get._2.content.toOption.get.asInstanceOf[LangLongValue].value)
    assert("callMyFunc2" == elseIf.content.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

}
