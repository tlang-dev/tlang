package io.sorne.tlang.astbuilder

import io.sorne.tlang.ast.model.let.{ModelNewArrayValue, ModelNewEntity, ModelNewEntityValue, ModelNewPrimitiveValue}
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildModelNewEntityTest extends AnyFunSuite {

  test("Test new entity without type") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let firstEntity {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val newEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[ModelNewEntity]
    assert("firstEntity".equals(newEntity.name))
  }

  test("Test new entity with type") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let firstEntity AnyEntity {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val newEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[ModelNewEntity]
    assert("firstEntity".equals(newEntity.name))
    assert("AnyEntity".equals(newEntity.entity.`type`.get))
  }

  test("Test new entity with parameters") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |let firstEntity AnyEntity("myString", var1 ["elm1", "elm2"], newEntity NewEntity {
        |}) {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val newEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[ModelNewEntity]
    assert("firstEntity".equals(newEntity.name))
    assert("AnyEntity".equals(newEntity.entity.`type`.get))
    assert(newEntity.entity.params.get.head.attr.isEmpty)
    assert("\"myString\"".equals(newEntity.entity.params.get.head.value.asInstanceOf[ModelNewPrimitiveValue].getValue.value))
    assert("var1".equals(newEntity.entity.params.get(1).value.asInstanceOf[ModelNewArrayValue].attr.get))
    assert("\"elm1\"".equals(newEntity.entity.params.get(1).value.asInstanceOf[ModelNewArrayValue].tbl.get.head.value.asInstanceOf[ModelNewPrimitiveValue].getValue.value))
    assert("\"elm2\"".equals(newEntity.entity.params.get(1).value.asInstanceOf[ModelNewArrayValue].tbl.get.last.value.asInstanceOf[ModelNewPrimitiveValue].getValue.value))
    assert("newEntity".equals(newEntity.entity.params.get.last.attr.get))
    assert("NewEntity".equals(newEntity.entity.params.get.last.value.asInstanceOf[ModelNewEntityValue].`type`.get))
  }

}
