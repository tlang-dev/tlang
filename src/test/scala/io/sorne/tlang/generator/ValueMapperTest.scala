package io.sorne.tlang.generator

import io.sorne.tlang.ast.common.value.TLangString
import io.sorne.tlang.ast.tmpl.call.{TmplCallArray, TmplCallFunc, TmplCallObj, TmplCallVar}
import io.sorne.tlang.ast.tmpl.func.TmplFunc
import io.sorne.tlang.ast.tmpl.primitive.{TmplStringValue, TmplTextValue}
import io.sorne.tlang.ast.tmpl.{TmplGeneric, TmplImpl, TmplImplFor, TmplMultiValue, TmplPkg, TmplSetAttribute, TmplType, TmplUse, TmplVar}
import org.scalatest.funsuite.AnyFunSuite

class ValueMapperTest extends AnyFunSuite {

  test("Replace String") {
    val values = Map("one" -> new TLangString("This is the first one"), "two" -> new TLangString("the second one"), "three" -> new TLangString("the last one"))
    val str = "${one} to replace, obviously this is ${two}, and finally, this is ${three}"
    val res = ValueMapper.mapID(str, values)
    assert("This is the first one to replace, obviously this is the second one, and finally, this is the last one" == res)
  }

  test("Replace String in entity") {

  }

  test("Replace in package") {
    val values = Map("one" -> new TLangString("Package1"), "two" -> new TLangString("Package2"))
    val res = ValueMapper.mapPkg(Some(new TmplPkg(List("${one}", "${two}"))), values).get
    assert("Package1" == res.parts.head)
    assert("Package2" == res.parts.last)
  }

  test("Replace in uses") {
    val values = Map("one" -> new TLangString("Package1"), "two" -> new TLangString("Package2"))
    val res = ValueMapper.mapUses(Some(List(TmplUse(List("${one}", "Package2")), new TmplUse(List("Package1", "${two}")))), values).get
    assert("Package1" == res.head.parts.head)
    assert("Package2" == res.head.parts.last)
    assert("Package1" == res.last.parts.head)
    assert("Package2" == res.last.parts.last)
  }

  test("Replace in impl") {
    val values = Map("name" -> new TLangString("MyImpl"))
    val impl = TmplImpl(None, None, "${name}", None, None, None)
    val res = ValueMapper.mapImpl(impl, values)
    assert("MyImpl" == res.name)
  }

  test("Replace in impl fors") {
    val values = Map("for1" -> new TLangString("String"), "for2" -> new TLangString("CharSequence"))
    val fors = Some(List(TmplImplFor("${for1}"), TmplImplFor("${for2}")))
    val res = ValueMapper.mapFors(fors, values).get
    assert("String" == res.head.name)
    assert("CharSequence" == res.last.name)
  }

  test("Replace func name") {
    val values = Map("name" -> new TLangString("MyFunc"))
    val func = TmplFunc(None, None, "${name}", None, None)
    val res = ValueMapper.mapExpression(func, values)
    assert("MyFunc" == res.asInstanceOf[TmplFunc].name)
  }

  test("Replace content with impl") {
    val values = Map("name" -> new TLangString("MyImpl"))
    val impl = Some(List(TmplImpl(None, None, "${name}", None, None, None)))
    val res = ValueMapper.mapContent(impl, values).get.head.asInstanceOf[TmplImpl]
    assert("MyImpl" == res.name)
  }

  test("Replace content with func") {
    val values = Map("name" -> new TLangString("MyFunc"))
    val func = Some(List(TmplFunc(None, None, "${name}", None, None)))
    val res = ValueMapper.mapContent(func, values).get.head.asInstanceOf[TmplFunc]
    assert("MyFunc" == res.name)
  }

  test("Replace content with call var") {
    val values = Map("name" -> new TLangString("MyVar"))
    val call = Some(List(TmplCallObj(List(TmplCallVar("${name}")))))
    val res = ValueMapper.mapContent(call, values).get.head
    assert("MyVar" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name)
  }

  test("Replace expressions with call var") {
    val values = Map("name" -> new TLangString("MyVar"))
    val call = Some(List(TmplCallObj(List(TmplCallVar("${name}")))))
    val res = ValueMapper.mapExpressions(call, values).get.head
    assert("MyVar" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name)
  }

  test("Replace expressions with none") {
    assert(ValueMapper.mapExpressions(None, Map()).isEmpty)
  }

  test("Replace call array") {
    val values = Map("name" -> new TLangString("MyCall"))
    val call = TmplCallObj(List(TmplCallArray("${name}", TmplStringValue("position1"))))
    val res = ValueMapper.mapCallObj(call, values)
    assert("MyCall" == res.calls.head.asInstanceOf[TmplCallArray].name)
  }

  test("Replace call var") {
    val values = Map("name" -> new TLangString("MyVar"))
    val call = TmplCallObj(List(TmplCallVar("${name}")))
    val res = ValueMapper.mapExpression(call, values)
    assert("MyVar" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name)
  }

  test("Replace call func") {
    val values = Map("name" -> new TLangString("MyFunc"))
    val call = TmplCallObj(List(TmplCallFunc("${name}", None)))
    val res = ValueMapper.mapExpression(call, values)
    assert("MyFunc" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name)
  }

  test("Var") {
    val values = Map("name" -> new TLangString("MyVar"),
      "type" -> new TLangString("String"),
      "value" -> new TLangString("MyValue"))
    val newVar = TmplVar(None, None, "${name}", TmplType("${type}"), TmplMultiValue(List(TmplStringValue("${value}"))))
    val res = ValueMapper.mapExpression(newVar, values).asInstanceOf[TmplVar]
    assert("MyVar" == res.name)
    assert("String" == res.`type`.name)
    assert("MyValue" == res.value.asInstanceOf[TmplMultiValue].values.head.asInstanceOf[TmplStringValue].value)
  }

  test("Generic type") {
    val values = Map("type" -> new TLangString("List"), "generic" -> new TLangString("String"))
    val newType = TmplType("${type}", Some(TmplGeneric(List(TmplType("${generic}")))))
    val res = ValueMapper.mapType(newType, values)
    assert("List" == res.name)
    assert("String" == res.generic.get.types.head.name)
  }

  test("Replace set attributes") {
    val values = Map("name" -> new TLangString("MyAttr"), "value" -> new TLangString("MyValue"))
    val attr = Some(List(TmplSetAttribute(Some("${name}"), TmplTextValue("${value}"))))
    val res = ValueMapper.mapSetAttributes(attr, values).get.head
    assert("MyAttr" == res.name.get)
    assert("MyValue" == res.value.asInstanceOf[TmplTextValue].value)
  }

  test("Replace set attributes with none") {
    assert(ValueMapper.mapSetAttributes(None, Map()).isEmpty)
  }

}
