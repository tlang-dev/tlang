package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.value.{ArrayValue, AssignVar, EntityValue, TLangString}
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildModelNewEntityTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Test new entity without type") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |let firstEntity = {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val newEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[AssignVar]
    assert("firstEntity".equals(newEntity.name))
  }

  test("Test new entity with type") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |let firstEntity :AnyEntity = {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val newEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[AssignVar]
    assert("firstEntity".equals(newEntity.name))
    assert("AnyEntity".equals(newEntity.`type`.get.getType))
  }

  test("Test new entity with parameters") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |let firstEntity :AnyEntity = {
        |"myString"
        |var1 = ["elm1", "elm2"]
        |newEntity :NewEntity = {
        |}
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val newEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[AssignVar]
    val params = newEntity.value.getElement.content.toOption.get.asInstanceOf[EntityValue].attrs
    assert("firstEntity".equals(newEntity.name))
    assert("AnyEntity".equals(newEntity.`type`.get.getType))
    assert(params.get.head.attr.isEmpty)
    assert("myString".equals(params.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement))
    assert("var1".equals(params.get(1).attr.get))
    assert("elm1".equals(params.get(1).value.content.toOption.get.asInstanceOf[ArrayValue].tbl.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement))
    assert("elm2".equals(params.get(1).value.content.toOption.get.asInstanceOf[ArrayValue].tbl.get.last.value.content.toOption.get.asInstanceOf[TLangString].getElement))
    assert("newEntity".equals(params.get.last.attr.get))
    assert("NewEntity".equals(params.get.last.`type`.get.getType))
  }

}
