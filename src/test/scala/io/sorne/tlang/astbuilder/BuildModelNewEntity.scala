package io.sorne.tlang.astbuilder

import io.sorne.tlang.ast.model.`new`.ModelNewEntity
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildModelNewEntity extends AnyFunSuite {

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
    assert("AnyEntity".equals(newEntity.`type`.get))
  }

}
