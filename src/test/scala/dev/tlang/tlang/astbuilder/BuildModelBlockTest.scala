package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, TLangString}
import dev.tlang.tlang.ast.model.set._
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.{TLangLexer, TLangParser}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite

class BuildModelBlockTest extends AnyFunSuite {

  val fakeContext: ContextResource = ContextResource("", "", "", "")

  test("Test setting model entity") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity" == setEntity.name)
  }

  test("Test setting model entity with attributes") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity (attr1 String, Type2, Type3[]) {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity" == setEntity.name)
    assert("attr1" == setEntity.params.get.head.attr.get)
    assert("String" == setEntity.params.get.head.value.asInstanceOf[ModelSetType].`type`)
    assert(setEntity.params.get(1).attr.isEmpty)
    assert("Type2" == setEntity.params.get(1).value.asInstanceOf[ModelSetType].`type`)
    assert("Type3" == setEntity.params.get.last.value.asInstanceOf[ModelSetArray].array)
  }

  test("Test setting model entity with types") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |var1 String
        |var2 Type2[]
        |Type3
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity" == setEntity.name)
    assert("var1" == setEntity.attrs.get.head.attr.get)
    assert("String" == setEntity.attrs.get.head.value.asInstanceOf[ModelSetType].`type`)
    assert("var2" == setEntity.attrs.get(1).attr.get)
    assert("Type2" == setEntity.attrs.get(1).value.asInstanceOf[ModelSetArray].array)
    assert(setEntity.attrs.get.last.attr.isEmpty)
    assert("Type3" == setEntity.attrs.get.last.value.asInstanceOf[ModelSetType].`type`)
  }

  test("Function as parameter") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity (attr1 (String, Int[]):(Bool), (String):(Int[], Bool)) {
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    val params1 = setEntity.params.get.head.value.asInstanceOf[ModelSetFuncDef].params.get
    val returns1 = setEntity.params.get.head.value.asInstanceOf[ModelSetFuncDef].returns.get
    val params2 = setEntity.params.get.last.value.asInstanceOf[ModelSetFuncDef].params.get
    val returns2 = setEntity.params.get.last.value.asInstanceOf[ModelSetFuncDef].returns.get
    assert("firstEntity" == setEntity.name)
    assert("attr1" == setEntity.params.get.head.attr.get)
    assert("String" == params1.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == params1.last.value.asInstanceOf[ModelSetArray].array)
    assert("Bool" == returns1.head.value.asInstanceOf[ModelSetType].`type`)

    assert(setEntity.params.get.last.attr.isEmpty)
    assert("String" == params2.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == returns2.head.value.asInstanceOf[ModelSetArray].array)
    assert("Bool" == returns2.last.value.asInstanceOf[ModelSetType].`type`)
  }

  test("Function as attribute") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |attr1 (String, Int[]):(Bool)
        |(String):(Int[], Bool)
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    val attr1 = setEntity.attrs.get.head.value.asInstanceOf[ModelSetFuncDef].params.get
    val returns1 = setEntity.attrs.get.head.value.asInstanceOf[ModelSetFuncDef].returns.get
    val attr2 = setEntity.attrs.get.last.value.asInstanceOf[ModelSetFuncDef].params.get
    val returns2 = setEntity.attrs.get.last.value.asInstanceOf[ModelSetFuncDef].returns.get
    assert("firstEntity" == setEntity.name)
    assert("attr1" == setEntity.attrs.get.head.attr.get)
    assert("String" == attr1.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == attr1.last.value.asInstanceOf[ModelSetArray].array)
    assert("Bool" == returns1.head.value.asInstanceOf[ModelSetType].`type`)

    assert(setEntity.attrs.get.last.attr.isEmpty)
    assert("String" == attr2.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == returns2.head.value.asInstanceOf[ModelSetArray].array)
    assert("Bool" == returns2.last.value.asInstanceOf[ModelSetType].`type`)
  }

  test("Function as parameter and attribute without parameters") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity (():(String, Int[])){
        |attr1 ():(Bool, Int[])
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    val returns1 = setEntity.params.get.last.value.asInstanceOf[ModelSetFuncDef].returns.get
    val returns2 = setEntity.attrs.get.last.value.asInstanceOf[ModelSetFuncDef].returns.get
    assert(setEntity.params.get.head.value.asInstanceOf[ModelSetFuncDef].params.isEmpty)
    assert("String" == returns1.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == returns1.last.value.asInstanceOf[ModelSetArray].array)

    assert(setEntity.attrs.get.head.value.asInstanceOf[ModelSetFuncDef].params.isEmpty)
    assert("Bool" == returns2.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == returns2.last.value.asInstanceOf[ModelSetArray].array)
  }

  test("Function as parameter and attribute without returns") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity ((String, Int[])){
        |attr1 (String, Int[])
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    val params1 = setEntity.params.get.head.value.asInstanceOf[ModelSetFuncDef].params.get
    val attr1 = setEntity.attrs.get.head.value.asInstanceOf[ModelSetFuncDef].params.get

    assert("String" == params1.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == params1.last.value.asInstanceOf[ModelSetArray].array)
    assert(setEntity.params.get.head.value.asInstanceOf[ModelSetFuncDef].returns.isEmpty)

    assert("String" == attr1.head.value.asInstanceOf[ModelSetType].`type`)
    assert("Int" == attr1.last.value.asInstanceOf[ModelSetArray].array)
    assert(setEntity.attrs.get.head.value.asInstanceOf[ModelSetFuncDef].returns.isEmpty)
  }

  test("Reference as parameter") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity (param1 &entity1){
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity" == setEntity.name)
    assert("param1" == setEntity.params.get.head.attr.get)
    assert(setEntity.params.get.head.value.asInstanceOf[ModelSetRef].currying.isEmpty)
    assert("entity1" == setEntity.params.get.head.value.asInstanceOf[ModelSetRef].refs.head)
  }

  test("Reference as parameter with multiple refs") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity (param1 &entity1.attr1.subAttr1){
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity" == setEntity.name)
    assert("param1" == setEntity.params.get.head.attr.get)
    assert(setEntity.params.get.head.value.asInstanceOf[ModelSetRef].currying.isEmpty)
    assert("entity1" == setEntity.params.get.head.value.asInstanceOf[ModelSetRef].refs.head)
    assert("attr1" == setEntity.params.get.head.value.asInstanceOf[ModelSetRef].refs(1))
    assert("subAttr1" == setEntity.params.get.head.value.asInstanceOf[ModelSetRef].refs.last)
  }

  test("Reference as attribute") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |attr1 &entity1
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity" == setEntity.name)
    assert("attr1" == setEntity.attrs.get.head.attr.get)
    assert(setEntity.attrs.get.head.value.asInstanceOf[ModelSetRef].currying.isEmpty)
    assert("entity1" == setEntity.attrs.get.head.value.asInstanceOf[ModelSetRef].refs.head)
  }

  test("Reference as attribute with multiple refs") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |attr1 &entity1.attr1.subAttr1
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    assert("firstEntity" == setEntity.name)
    assert("attr1" == setEntity.attrs.get.head.attr.get)
    assert(setEntity.attrs.get.head.value.asInstanceOf[ModelSetRef].currying.isEmpty)
    assert("entity1" == setEntity.attrs.get.head.value.asInstanceOf[ModelSetRef].refs.head)
    assert("attr1" == setEntity.attrs.get.head.value.asInstanceOf[ModelSetRef].refs(1))
    assert("subAttr1" == setEntity.attrs.get.head.value.asInstanceOf[ModelSetRef].refs.last)
  }

  test("Reference: calling a function") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |&anyFunc("myValue", ["1", "2", "3"])
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val setEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity]
    val ref = setEntity.attrs.get.head.value.asInstanceOf[ModelSetRef]
    val array = ref.currying.get.head.values.last.asInstanceOf[Operation].content.toOption.get.asInstanceOf[ArrayValue].tbl.get
    assert("anyFunc" == ref.refs.head)
    assert("myValue" == ref.currying.get.head.values.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("1" == array.head.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("2" == array(1).value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("3" == array.last.value.content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Model set entity with impl") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |impl {
        |var1 String
        |var2 Type2[]
        |Type3
        |}
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity].attrs.get.head.value.asInstanceOf[ModelSetImpl].attrs.get
    assert("var1" == impl.head.attr.get)
    assert("String" == impl.head.value.asInstanceOf[ModelSetType].`type`)
    assert("var2" == impl(1).attr.get)
    assert("Type2" == impl(1).value.asInstanceOf[ModelSetArray].array)
    assert("Type3" == impl.last.value.asInstanceOf[ModelSetType].`type`)
  }

  test("Model set entity with impl array") {
    val lexer = new TLangLexer(CharStreams.fromString(
      """model {
        |set firstEntity {
        |impl[]
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLangParser(tokens)
    val impl = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[ModelSetEntity].attrs.get.head.value
    assert(impl.isInstanceOf[ModelSetImplArray])
  }

}
