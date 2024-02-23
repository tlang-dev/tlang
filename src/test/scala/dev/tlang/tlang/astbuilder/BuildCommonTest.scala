package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.{CommonLexer, TLang}
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core
import tlang.internal.ContextResource

class BuildCommonTest extends AnyFunSuite {

  val fakeContext: ContextResource = new ContextResource(new core.String(""), new core.String(""), new core.String(""), new core.String(""))

  test("Impl in entity") {
    val lexer = new CommonLexer(CharStreams.fromString(
      """model {
        |let firstEntity = {
        |impl: MyEntity {
        |"myString",
        |var1 = ["elm1", "elm2"],
        |newEntity :NewEntity = {
        |}
        |}
        |}
        |}""".stripMargin))
    val tokens = new CommonTokenStream(lexer)
    val parser = new TLang(tokens)
    val newEntity = BuildModelBlock.build(fakeContext, parser.modelBlock()).content.get.head.asInstanceOf[AssignVar]
    val impl = newEntity.value.getElement.content.toOption.get.asInstanceOf[EntityValue].attrs.get.head.value.content.toOption.get.asInstanceOf[EntityImpl]
    assert("firstEntity" == newEntity.name)
    assert("MyEntity" == impl.`type`.get)
    assert("myString" == impl.attrs.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("var1" == impl.attrs.get(1).attr.get)
    assert("elm1" == impl.attrs.get(1).value.content.toOption.get.asInstanceOf[ArrayValue].tbl.get.head.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("elm2" == impl.attrs.get(1).value.content.toOption.get.asInstanceOf[ArrayValue].tbl.get.last.value.content.toOption.get.asInstanceOf[TLangString].getElement)
    assert("newEntity" == impl.attrs.get.last.attr.get)
    assert("NewEntity" == impl.attrs.get.last.`type`.get.getType.getType.toString)
  }
}
