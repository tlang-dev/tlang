package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperCallArrayObject, HelperCallObject, HelperCallVarObject}
import io.sorne.tlang.ast.model.`new`._
import org.scalatest.funsuite.AnyFunSuite

class ExecCallObjectTest extends AnyFunSuite {

  test("Get simple variable") {
    val context = Context(variables = Map("var1" -> ModelNewEntityValue(Some("MyEntity"))))
    val statement = HelperCallObject(List(HelperCallVarObject("var1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.isInstanceOf[ModelNewEntityValue])
    assert("MyEntity".equals(res.asInstanceOf[ModelNewEntityValue].`type`.get))
  }

  test("Get variable from array by index") {
    val array = ModelNewArrayValue(Some("MyArray"), Some(List(
      ModelNewAttribute(value = ModelNewPrimitiveValue(value = "value1")),
      ModelNewAttribute(value = ModelNewPrimitiveValue(value = "value2")),
      ModelNewAttribute(value = ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(variables = Map("var1" -> array))
    val statement = HelperCallObject(List(HelperCallArrayObject("var1", "1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Get variable from array by name") {
    val array = ModelNewArrayValue(Some("MyArray"), Some(List(
      ModelNewAttribute(Some("myPosition1"), ModelNewPrimitiveValue(value = "value1")),
      ModelNewAttribute(Some("myPosition2"), ModelNewPrimitiveValue(value = "value2")),
      ModelNewAttribute(Some("myPosition3"), ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(variables = Map("var1" -> array))
    val statement = HelperCallObject(List(HelperCallArrayObject("var1", "myPosition2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Get variable from params in entity") {
    val myEntity = ModelNewEntityValue(Some("MyEntity"), Some(List(
      ModelNewAttribute(Some("attr1"), ModelNewPrimitiveValue(value = "value1")),
      ModelNewAttribute(Some("attr2"), ModelNewPrimitiveValue(value = "value2")),
      ModelNewAttribute(Some("attr3"), ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(variables = Map("var1" -> myEntity))
    val statement = HelperCallObject(List(HelperCallVarObject("var1"), HelperCallVarObject("attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.asInstanceOf[ModelNewPrimitiveValue].value))
  }

  test("Get variable from attrs in entity") {
    val myEntity = ModelNewEntityValue(Some("MyEntity"), None, Some(List(
      ModelNewAttribute(Some("attr1"), ModelNewPrimitiveValue(value = "value1")),
      ModelNewAttribute(Some("attr2"), ModelNewPrimitiveValue(value = "value2")),
      ModelNewAttribute(Some("attr3"), ModelNewPrimitiveValue(value = "value3"))
    )))
    val context = Context(variables = Map("var1" -> myEntity))
    val statement = HelperCallObject(List(HelperCallVarObject("var1"), HelperCallVarObject("attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.isInstanceOf[ModelNewPrimitiveValue])
    assert("value2".equals(res.asInstanceOf[ModelNewPrimitiveValue].value))
  }

}
