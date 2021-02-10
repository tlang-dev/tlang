package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangString}
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallObjectTest extends AnyFunSuite {

  test("Get simple variable") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> EntityValue(Some("MyEntity"))))))
    val statement = CallObject(List(CallVarObject("var1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[EntityValue])
    assert("MyEntity".equals(res.head.asInstanceOf[EntityValue].`type`.get))
  }

  test("Get variable from array by index") {
    val array = ArrayValue(Some(List(
      SimpleAttribute(value = new TLangString(value = "value1")),
      SimpleAttribute(value = new TLangString(value = "value2")),
      SimpleAttribute(value = new TLangString(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array))))
    val statement = CallObject(List(CallArrayObject("var1", new TLangLong(1))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Get variable from array by name") {
    val array = ArrayValue(Some(List(
      SimpleAttribute(Some("myPosition1"), value = new TLangString(value = "value1")),
      SimpleAttribute(Some("myPosition2"), value = new TLangString(value = "value2")),
      SimpleAttribute(Some("myPosition3"), value = new TLangString(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array))))
    val statement = CallObject(List(CallArrayObject("var1", new TLangString("myPosition2"))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Get variable from params in entity") {
    val myEntity = EntityValue(Some("MyEntity"), Some(List(
      ComplexAttribute(Some("attr1"), None, new TLangString(value = "value1")),
      ComplexAttribute(Some("attr2"), None, new TLangString(value = "value2")),
      ComplexAttribute(Some("attr3"), None, new TLangString(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(List(CallVarObject("var1"), CallVarObject("attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Get variable from attrs in entity") {
    val myEntity = EntityValue(Some("MyEntity"), None, Some(List(
      ComplexAttribute(Some("attr1"), None, new TLangString(value = "value1")),
      ComplexAttribute(Some("attr2"), None, new TLangString(value = "value2")),
      ComplexAttribute(Some("attr3"), None, new TLangString(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(List(CallVarObject("var1"), CallVarObject("attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Call function with one parameter") {
    val callInsideFunc = CallObject(List(CallVarObject("valToReturn")))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = block)
    val caller = SetAttribute(value = CallObject(List(CallVarObject("var1"))))
    val statement = CallObject(List(CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(Some(List(caller))))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Call function with currying") {
    val callInsideFunc = MultiValue(List(
      CallObject(List(CallVarObject("valToReturn"))),
      CallObject(List(CallVarObject("valToReturn2")))))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(
      HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))),
      HelperCurrying(List(HelperParam(Some("valToReturn2"), HelperObjType("String")))))), None, block = block)
    val caller = SetAttribute(value = CallObject(List(CallVarObject("var1"))))
    val caller2 = SetAttribute(value = CallObject(List(CallVarObject("var2"))))
    val statement = CallObject(List(CallFuncObject(Some("myFunc"), Some(List(
      CallFuncParam(Some(List(caller))),
      CallFuncParam(Some(List(caller2))),
    )))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue"), "var2" -> new TLangString("MyValue2")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getValue))
    assert("MyValue2".equals(res.last.asInstanceOf[TLangString].getValue))
  }

  test("Call function from entity") {
    val callInsideFunc = CallObject(List(CallVarObject("valToReturn")))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = block)
    val caller = SetAttribute(value = CallObject(List(CallVarObject("var1"))))
    val attrStatement = CallObject(List(CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(Some(List(caller))))))))
    val myEntity = EntityValue(Some("MyEntity"), None, Some(List(
      ComplexAttribute(Some("attr1"), None, attrStatement),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue"), "myEntity" -> myEntity), functions = mutable.Map("myFunc" -> funcDef))))
    val statement = CallObject(List(CallVarObject("myEntity"), CallVarObject("attr1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Call array from entity") {
    val array = ArrayValue(Some(List(
      SimpleAttribute(Some("myPosition1"), None, new TLangString(value = "value1")),
      SimpleAttribute(Some("myPosition2"), None, new TLangString(value = "value2")),
      SimpleAttribute(Some("myPosition3"), None, new TLangString(value = "value3"))
    )))
    val attrStatement = CallObject(List(CallArrayObject("var1", new TLangString("myPosition2"))))
    val myEntity = EntityValue(Some("MyEntity"), None, Some(List(
      ComplexAttribute(Some("attr1"), None, attrStatement),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array, "myEntity" -> myEntity))))
    val statement = CallObject(List(CallVarObject("myEntity"), CallVarObject("attr1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Function called inside a function") {
    val callInsideFunc2 = CallObject(List(CallVarObject("valToReturn")))
    val blockFunc2 = HelperContent(Some(List(callInsideFunc2)))
    val funcDef2 = HelperFunc("myFunc2", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = blockFunc2)

    val caller = SetAttribute(value = CallObject(List(CallVarObject("var1"))))
    val callInsideFunc1 = CallObject(List(CallFuncObject(Some("myFunc2"), Some(List(CallFuncParam(Some(List(caller))))))))
    val blockFunc1 = HelperContent(Some(List(callInsideFunc1)))
    val funcDef1 = HelperFunc("myFunc1", None, Some(List(HelperObjType("String"))), block = blockFunc1)

    val statement = CallObject(List(CallFuncObject(Some("myFunc1"), None)))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), functions = mutable.Map("myFunc2" -> funcDef2, "myFunc1" -> funcDef1))))
    val res = ExecCallObject.run(statement, context)
    val res2 = res.toOption.get.get
    assert("MyValue".equals(res2.head.asInstanceOf[TLangString].getValue))
  }

  test("Call function in other resources") {
    val callInsideFunc = CallObject(List(CallVarObject("valToReturn")))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = block)
    val caller = SetAttribute(value = CallObject(List(CallVarObject("var1"))))
    val statement = CallObject(List(CallVarObject("myResource"), CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(Some(List(caller))))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), functions = mutable.Map("myResource/myFunc" -> funcDef))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  /* For now the returned functions are directly executed, a reference will be needed.
  test("Function returning a nested function") {
    val callInsideFunc2 = HelperCallObject(List(HelperCallVarObject("valToReturn")))
    val blockFunc2 = HelperContent(Some(List(callInsideFunc2)))
    val funcDef2 = HelperFunc("myFunc2", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = blockFunc2)

//    val callInsideFunc1 = HelperCallObject(List(HelperCallFuncObject(Some("myFunc2"), None)))
    val blockFunc1 = HelperContent(Some(List(funcDef2)))
    val funcDef1 = HelperFunc("myFunc1", None, Some(List(HelperFuncType(None, Some(List(HelperObjType("String")))))), block = blockFunc1)

    val caller = HelperCallObject(List(HelperCallVarObject("var1")))
    val statement = HelperCallObject(List(HelperCallFuncObject(Some("myFunc1"), None), HelperCallFuncObject(None, Some(List(HelperCallFuncParam(List(caller)))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), functions = mutable.Map("myFunc1" -> funcDef1))))
    val res = ExecCallObject.run(statement, context)
      val res2 = res.toOption.get.get
    assert("MyValue".equals(res2.head.asInstanceOf[TLangString].getValue))
  }*/

}
