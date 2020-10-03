package io.sorne.tlang.astbuilder

import io.sorne.tlang.ast.model.set.{ModelSetEntity, ModelSetType}
import io.sorne.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildModelBlockTest extends AnyFunSuite {

  test("Test setting model entity") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity".equals(setEntity.name))
  }

  test("Test setting model entity with attributes") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity (attr1 String, Type2, Type3<Generic1>) {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity".equals(setEntity.name))
    assert("attr1".equals(setEntity.params.get.head.attr.get))
    assert("String".equals(setEntity.params.get.head.value.asInstanceOf[ModelSetType].`type`))
    assert(setEntity.params.get(1).attr.isEmpty)
    assert("Type2".equals(setEntity.params.get(1).value.asInstanceOf[ModelSetType].`type`))
    assert("Type3".equals(setEntity.params.get.last.value.asInstanceOf[ModelSetType].`type`))
    assert("Generic1".equals(setEntity.params.get.last.value.asInstanceOf[ModelSetType].generics.get.types.head.`type`))
  }

  test("Test setting model entity with types") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |var1 String
        |var2 Type2<Generic1>
        |Type3
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity".equals(setEntity.name))
    assert("var1".equals(setEntity.attrs.get.head.attr.get))
    assert("String".equals(setEntity.attrs.get.head.value.asInstanceOf[ModelSetType].`type`))
    assert("var2".equals(setEntity.attrs.get(1).attr.get))
    assert("Type2".equals(setEntity.attrs.get(1).value.asInstanceOf[ModelSetType].`type`))
    assert("Generic1".equals(setEntity.attrs.get(1).value.asInstanceOf[ModelSetType].generics.get.types.head.`type`))
    assert(setEntity.attrs.get.last.attr.isEmpty)
    assert("Type3".equals(setEntity.attrs.get.last.value.asInstanceOf[ModelSetType].`type`))
  }

}
