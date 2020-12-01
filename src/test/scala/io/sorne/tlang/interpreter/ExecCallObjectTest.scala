package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.common.call._
import io.sorne.tlang.ast.common.value
import io.sorne.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, MultiValue, TLangString}
import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.model.let._
import io.sorne.tlang.interpreter.context.{Context, Scope}
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
    val array = CommonArrayValue(Some("MyArray"), Some(List(
      CommonAttribute(value = ModelNewPrimitiveValue(value = "value1")),
      value.CommonAttribute(value = ModelNewPrimitiveValue(value = "value2")),
      value.CommonAttribute(value = ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array))))
    val statement = CallObject(List(CallArrayObject("var1", CallObject(List(HelperNewInt(1))))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.head.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Get variable from array by name") {
    val array = CommonArrayValue(Some("MyArray"), Some(List(
      value.CommonAttribute(Some("myPosition1"), ModelNewPrimitiveValue(value = "value1")),
      value.CommonAttribute(Some("myPosition2"), ModelNewPrimitiveValue(value = "value2")),
      value.CommonAttribute(Some("myPosition3"), ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array))))
    val statement = CallObject(List(CallArrayObject("var1", CallObject(List(HelperNewString("myPosition2"))))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.head.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Get variable from params in entity") {
    val myEntity = EntityValue(Some("MyEntity"), Some(List(
      value.CommonAttribute(Some("attr1"), ModelNewPrimitiveValue(value = "value1")),
      value.CommonAttribute(Some("attr2"), ModelNewPrimitiveValue(value = "value2")),
      value.CommonAttribute(Some("attr3"), ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(List(CallVarObject("var1"), CallVarObject("attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.head.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Get variable from attrs in entity") {
    val myEntity = EntityValue(Some("MyEntity"), None, Some(List(
      value.CommonAttribute(Some("attr1"), ModelNewPrimitiveValue(value = "value1")),
      value.CommonAttribute(Some("attr2"), ModelNewPrimitiveValue(value = "value2")),
      value.CommonAttribute(Some("attr3"), ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(List(CallVarObject("var1"), CallVarObject("attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.head.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Call function with one parameter") {
    val callInsideFunc = CallObject(List(CallVarObject("valToReturn")))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = block)
    val caller = CallObject(List(CallVarObject("var1")))
    val statement = CallObject(List(CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(List(caller)))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Call function with currying") {
    val callInsideFunc = MultiValue(List(
      Left(CallObject(List(CallVarObject("valToReturn")))),
      Left(CallObject(List(CallVarObject("valToReturn2"))))))
    val block = HelperContent(Some(List(callInsideFunc)))
    val funcDef = HelperFunc("myFunc", Some(List(
      HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))),
      HelperCurrying(List(HelperParam(Some("valToReturn2"), HelperObjType("String")))))), None, block = block)
    val caller = CallObject(List(CallVarObject("var1")))
    val caller2 = CallObject(List(CallVarObject("var2")))
    val statement = CallObject(List(CallFuncObject(Some("myFunc"), Some(List(
      CallFuncParam(List(caller)),
      CallFuncParam(List(caller2)),
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
    val caller = CallObject(List(CallVarObject("var1")))
    val attrStatement = CallObject(List(CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(List(caller)))))))
    val myEntity = EntityValue(Some("MyEntity"), None, Some(List(
      ModelNewAttribute(Some("attr1"), attrStatement),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString("MyValue"), "myEntity" -> myEntity), functions = mutable.Map("myFunc" -> funcDef))))
    val statement = CallObject(List(CallVarObject("myEntity"), CallVarObject("attr1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getValue))
  }

  test("Call array from entity") {
    val array = CommonArrayValue(Some("MyArray"), Some(List(
      value.CommonAttribute(Some("myPosition1"), ModelNewPrimitiveValue(value = "value1")),
      value.CommonAttribute(Some("myPosition2"), ModelNewPrimitiveValue(value = "value2")),
      value.CommonAttribute(Some("myPosition3"), ModelNewPrimitiveValue(value = "value3"))
    )))
    val attrStatement = CallObject(List(CallArrayObject("var1", CallObject(List(HelperNewString("myPosition2"))))))
    val myEntity = EntityValue(Some("MyEntity"), None, Some(List(
      ModelNewAttribute(Some("attr1"), attrStatement),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array, "myEntity" -> myEntity))))
    val statement = CallObject(List(CallVarObject("myEntity"), CallVarObject("attr1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.head.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Function called inside a function") {
    val callInsideFunc2 = CallObject(List(CallVarObject("valToReturn")))
    val blockFunc2 = HelperContent(Some(List(callInsideFunc2)))
    val funcDef2 = HelperFunc("myFunc2", Some(List(HelperCurrying(List(HelperParam(Some("valToReturn"), HelperObjType("String")))))), None, block = blockFunc2)

    val caller = CallObject(List(CallVarObject("var1")))
    val callInsideFunc1 = CallObject(List(CallFuncObject(Some("myFunc2"), Some(List(CallFuncParam(List(caller)))))))
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
    val caller = CallObject(List(CallVarObject("var1")))
    val statement = CallObject(List(CallVarObject("myResource"), CallFuncObject(Some("myFunc"), Some(List(CallFuncParam(List(caller)))))))
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
