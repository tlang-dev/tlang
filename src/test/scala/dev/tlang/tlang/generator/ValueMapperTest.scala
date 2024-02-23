package dev.tlang.tlang.generator

import org.scalatest.funsuite.AnyFunSuite

class ValueMapperTest extends AnyFunSuite {

 /* test("Replace String") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("one" -> new TLangString(None, "This is the replacement"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val res = ValueMapper.mapID(new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "one")))), context)
    assert("This is the replacement" == res.id)
  }

  test("Replace String in entity") {

  }

  test("Replace in package") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("one" -> new TLangString(None, "Package1"), "two" -> new TLangString(None, "Package2"))
    val context = Context(List(Scope("",values, mutable.Map(), mutable.Map())))
    val res = ValueMapper.mapPkg(
      Some(
        LangPkg(None, List(
          new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "one")))),
          new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "two"))))
        ))), context).get
    assert("Package1" == res.parts.head.toString)
    assert("Package2" == res.parts.last.toString)
  }

  test("Replace in uses") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("one" -> new TLangString(None, "Package1"), "two" -> new TLangString(None, "Package2"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val res = ValueMapper.mapUses(
      Some(
        List(
          LangUse(None, List(new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "one")))), TmplStringID(None, "Package2"))),
          LangUse(None, List(new TmplStringId(None, "Package1"), new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "two")))))))), context).get
    assert("Package1" == res.head.parts.head.toString)
    assert("Package2" == res.head.parts.last.toString)
    assert("Package1" == res.last.parts.head.toString)
    assert("Package2" == res.last.parts.last.toString)
  }

  test("Replace in impl") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyImpl"))
    val context = Context(List(Scope("",values, mutable.Map(), mutable.Map())))
    val impl = LangImpl(None, None, None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None, None)
    val res = ValueMapper.mapImpl(impl, context)
    assert("MyImpl" == res.name.toString)
  }

  test("Replace in impl fors") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("for1" -> new TLangString(None, "String"), "for2" -> new TLangString(None, "CharSequence"))
    val context = Context(List(Scope("",values, mutable.Map(), mutable.Map())))
    val fors = Some(LangImplFor(None, None, List(LangType(None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "for1"))))), LangType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "for2")))), isArray = false))))
    val res = ValueMapper.mapFors(fors, context).get
    assert("String" == res.types.head.name.toString)
    assert("CharSequence" == res.types.last.name.toString)
  }

  test("Replace func name") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyFunc"))
    val context = Context(List(Scope("",values, mutable.Map(), mutable.Map())))
    val func = LangFunc(None, None, None, None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None)
    val res = ValueMapper.mapExpression(func, context)
    assert("MyFunc" == res.asInstanceOf[LangFunc].name.toString)
  }

  test("Replace content with impl") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyImpl"))
    val context = Context(List(Scope("",values, mutable.Map(), mutable.Map())))
    val impl = Some(List(LangImpl(None, None, None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None, None)))
    val res = ValueMapper.mapContents(impl, context).get.head.asInstanceOf[LangImpl]
    assert("MyImpl" == res.name.toString)
  }

  test("Replace content with func") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyFunc"))
    val context = Context(List(Scope("",values, mutable.Map(), mutable.Map())))
    val func = Some(List(LangFunc(None, None, None, None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name")))), None, None)))
    val res = ValueMapper.mapContents(func, context).get.head.asInstanceOf[LangFunc]
    assert("MyFunc" == res.name.toString)
  }

  test("Replace content with call var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val call = Some(List(LangCallObj(None, None, LangCallVar(None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name"))))), List())))
    val res = ValueMapper.mapContents(call, context).get.head
    assert("MyVar" == res.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar].name.toString)
  }

  test("Replace expressions with call var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val call = Some(List(LangCallObj(None, None, LangCallVar(None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name"))))), List())))
    val res = ValueMapper.mapExpressions(call, context).get.head
    assert("MyVar" == res.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar].name.toString)
  }

  test("Replace expressions with none") {
    assert(ValueMapper.mapExpressions(None, Context()).isEmpty)
  }

  test("Replace call array") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyCall"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val call = LangCallObj(None, None, LangCallArray(None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name")))), LangOperation(None, Right(LangStringValue(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "position1")))))))), List())
    val res = ValueMapper.mapCallObj(call, context)
    assert("MyCall" == res.calls.head.asInstanceOf[LangCallArray].name.toString)
  }

  test("Replace call var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val call = LangCallObj(None, None, LangCallVar(None, new TmplInterpretedId(None, call = CallObject(None, List(CallVarObject(None, "name"))))), List())
    val res = ValueMapper.mapExpression(call, context)
    assert("MyVar" == res.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallVar].name.toString)
  }

  test("Replace call func") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyFunc"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val call = LangCallObj(None, None, LangCallFunc(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), None), List())
    val res = ValueMapper.mapExpression(call, context)
    assert("MyFunc" == res.asInstanceOf[LangCallObj].calls.head.asInstanceOf[LangCallFunc].name.toString)
  }

  test("Var") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyVar"),
      "type" -> new TLangString(None, "String"),
      "value" -> new TLangString(None, "MyValue"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val newVar = LangVar(None, None, None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name")))), Some(LangType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "type")))))),
      Some(LangOperation(None, Right(LangMultiValue(None, List(LangStringValue(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "value")))))))))), isOptional = false)
    val res = ValueMapper.mapExpression(newVar, context).asInstanceOf[LangVar]
    assert("MyVar" == res.name.toString)
    assert("String" == res.`type`.get.name.toString)
    assert("MyValue" == res.value.get.content.toOption.get.asInstanceOf[LangMultiValue].values.head.asInstanceOf[LangStringValue].value.toString)
  }

  test("Generic type") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("type" -> new TLangString(None, "List"), "generic" -> new TLangString(None, "String"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val newType = LangType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "type")))), Some(LangGeneric(None, List(LangType(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "generic")))))))), isArray = false)
    val res = ValueMapper.mapType(newType, context)
    assert("List" == res.name.toString)
    assert("String" == res.generic.get.types.head.name.toString)
  }

  test("Replace set attributes") {
    val values: mutable.Map[String, Value[_]] = mutable.Map("name" -> new TLangString(None, "MyAttr"), "value" -> new TLangString(None, "MyValue"))
    val context = Context(List(Scope("", values, mutable.Map(), mutable.Map())))
    val attr = Some(List(LangSetAttribute(None, Some(TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "name"))))), LangOperation(None, Right(LangTextValue(None, TmplInterpretedID(None, call = CallObject(None, List(CallVarObject(None, "value"))))))))))
    val res = ValueMapper.mapSetAttributes(attr, context).get.head
    assert("MyAttr" == res.name.get.toString)
    assert("MyValue" == res.value.content.toOption.get.asInstanceOf[LangTextValue].value.toString)
  }

  test("Replace set attributes with none") {
    assert(ValueMapper.mapSetAttributes(None, Context()).isEmpty)
  }*/

}
