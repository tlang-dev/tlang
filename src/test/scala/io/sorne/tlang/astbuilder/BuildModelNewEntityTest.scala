package io.sorne.tlang.astbuilder

import io.sorne.tlang.ast.common.value.{ArrayValue, AssignVar, EntityValue, TLangString}
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildModelNewEntityTest extends AnyFunSuite {

  test("Test new entity without type") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let firstEntity = {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val newEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[AssignVar]
    assert("firstEntity".equals(newEntity.name))
  }

  test("Test new entity with type") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let firstEntity :AnyEntity = {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val newEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[AssignVar]
    assert("firstEntity".equals(newEntity.name))
    assert("AnyEntity".equals(newEntity.`type`.get))
  }

  test("Test new entity with parameters") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let firstEntity :AnyEntity = ("myString", var1 = ["elm1", "elm2"], newEntity :NewEntity = {
        |}) {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val newEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[AssignVar]
    val params = newEntity.value.getValue.asInstanceOf[EntityValue].params
    assert("firstEntity".equals(newEntity.name))
    assert("AnyEntity".equals(newEntity.`type`.get))
    assert(params.get.head.attr.isEmpty)
    assert("myString".equals(params.get.head.value.asInstanceOf[TLangString].getValue))
    assert("var1".equals(params.get(1).attr.get))
    assert("elm1".equals(params.get(1).value.asInstanceOf[ArrayValue].tbl.get.head.value.asInstanceOf[TLangString].getValue))
    assert("elm2".equals(params.get(1).value.asInstanceOf[ArrayValue].tbl.get.last.value.asInstanceOf[TLangString].getValue))
    assert("newEntity".equals(params.get.last.attr.get))
    assert("NewEntity".equals(params.get.last.`type`.get))
  }

}
