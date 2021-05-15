package dev.tlang.tlang.generator

import dev.tlang.tlang.ast.common.call.{CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.tmpl.call.{TmplCallArray, TmplCallFunc, TmplCallObj, TmplCallVar}
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.ast.tmpl.primitive.{TmplStringValue, TmplTextValue}
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.generator.mapper.ValueMapper
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ValueMapperTest extends AnyFunSuite {

  test("Replace String") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("one" -> new TLangString(None, "This is the replacement"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val res = ValueMapper.mapID(TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "one")))), context)
    assert("This is the replacement" == res.id)
  }

  test("Replace String in entity") {

  }

  test("Replace in package") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("one" -> new TLangString(None, "Package1"), "two" -> new TLangString(None, "Package2"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val res = ValueMapper.mapPkg(Some(new TmplPkg(List(TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "one")))), TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "two"))))))), context).get
    assert("Package1" == res.parts.head.toString)
    assert("Package2" == res.parts.last.toString)
  }

  test("Replace in uses") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("one" -> new TLangString(None, "Package1"), "two" -> new TLangString(None, "Package2"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val res = ValueMapper.mapUses(Some(List(TmplUse(List(TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "one")))), TmplStringID(None, "Package2"))), TmplUse(List(TmplStringID(None, "Package1"), TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "two")))))))), context).get
    assert("Package1" == res.head.parts.head.toString)
    assert("Package2" == res.head.parts.last.toString)
    assert("Package1" == res.last.parts.head.toString)
    assert("Package2" == res.last.parts.last.toString)
  }

  test("Replace in impl") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyImpl"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val impl = TmplImpl(None, None, None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None, None)
    val res = ValueMapper.mapImpl(impl, context)
    assert("MyImpl" == res.name.toString)
  }

  test("Replace in impl fors") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("for1" -> new TLangString(None, "String"), "for2" -> new TLangString(None, "CharSequence"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val fors = Some(TmplImplFor(None,None, List(TmplType(None,  TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "for1"))))),TmplType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "for2"))))))))
    val res = ValueMapper.mapFors(fors, context).get
    assert("String" == res.types.head.name.toString)
    assert("CharSequence" == res.types.last.name.toString)
  }

  test("Replace func name") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyFunc"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val func = TmplFunc(None, None, None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None)
    val res = ValueMapper.mapExpression(func, context)
    assert("MyFunc" == res.asInstanceOf[TmplFunc].name.toString)
  }

  test("Replace content with impl") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyImpl"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val impl = Some(List(TmplImpl(None, None, None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None, None)))
    val res = ValueMapper.mapContent(impl, context).get.head.asInstanceOf[TmplImpl]
    assert("MyImpl" == res.name.toString)
  }

  test("Replace content with func") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyFunc"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val func = Some(List(TmplFunc(None, None, None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None)))
    val res = ValueMapper.mapContent(func, context).get.head.asInstanceOf[TmplFunc]
    assert("MyFunc" == res.name.toString)
  }

  test("Replace content with call var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val call = Some(List(TmplCallObj(None, None, List(TmplCallVar(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))))))))
    val res = ValueMapper.mapContent(call, context).get.head
    assert("MyVar" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name.toString)
  }

  test("Replace expressions with call var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val call = Some(List(TmplCallObj(None, None, List(TmplCallVar(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))))))))
    val res = ValueMapper.mapExpressions(call, context).get.head
    assert("MyVar" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name.toString)
  }

  test("Replace expressions with none") {
    assert(ValueMapper.mapExpressions(None, Context()).isEmpty)
  }

  test("Replace call array") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyCall"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val call = TmplCallObj(None, None, List(TmplCallArray(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), TmplOperation(None, Right(TmplStringValue(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "position1"))))))))))
    val res = ValueMapper.mapCallObj(call, context)
    assert("MyCall" == res.calls.head.asInstanceOf[TmplCallArray].name.toString)
  }

  test("Replace call var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val call = TmplCallObj(None, None, List(TmplCallVar(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))))))
    val res = ValueMapper.mapExpression(call, context)
    assert("MyVar" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallVar].name.toString)
  }

  test("Replace call func") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyFunc"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val call = TmplCallObj(None, None, List(TmplCallFunc(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), None)))
    val res = ValueMapper.mapExpression(call, context)
    assert("MyFunc" == res.asInstanceOf[TmplCallObj].calls.head.asInstanceOf[TmplCallFunc].name.toString)
  }

  test("Var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"),
      "type" -> new TLangString(None, "String"),
      "value" -> new TLangString(None, "MyValue"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val newVar = TmplVar(None, None, None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), Some(TmplType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "type")))))),
      Some(TmplOperation(None, Right(TmplMultiValue(None, List(TmplStringValue(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "value")))))))))))
    val res = ValueMapper.mapExpression(newVar, context).asInstanceOf[TmplVar]
    assert("MyVar" == res.name.toString)
    assert("String" == res.`type`.get.name.toString)
    assert("MyValue" == res.value.get.content.toOption.get.asInstanceOf[TmplMultiValue].values.head.asInstanceOf[TmplStringValue].value.toString)
  }

  test("Generic type") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("type" -> new TLangString(None, "List"), "generic" -> new TLangString(None, "String"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val newType = TmplType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "type")))), Some(TmplGeneric(None, List(TmplType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "generic")))))))))
    val res = ValueMapper.mapType(newType, context)
    assert("List" == res.name.toString)
    assert("String" == res.generic.get.types.head.name.toString)
  }

  test("Replace set attributes") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyAttr"), "value" -> new TLangString(None, "MyValue"))
    val context = Context(List(Scope(values, mutable.Map(), mutable.Map())))
    val attr = Some(List(TmplSetAttribute(None, Some(TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name"))))), TmplOperation(None, Right(TmplTextValue(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "value"))))))))))
    val res = ValueMapper.mapSetAttributes(attr, context).get.head
    assert("MyAttr" == res.name.get.toString)
    assert("MyValue" == res.value.content.toOption.get.asInstanceOf[TmplTextValue].value.toString)
  }

  test("Replace set attributes with none") {
    assert(ValueMapper.mapSetAttributes(None, Context()).isEmpty)
  }

}
