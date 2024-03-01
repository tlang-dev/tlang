package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.common.value.{EntityValue, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core.Null

class CheckEntityTypeTest extends AnyFunSuite {

  test("Empty attributes") {
    val entityType = ModelSetEntity(None, ManualType("", "MyType"), None, Some(List()), None)
    val entity = EntityValue(None, Some(ObjType(None, None, ManualType("", "MyType"))), Some(List()))
    assert(CheckEntityType.checkEntityType(entity, entityType).isRight)
  }

  test("No type in entity") {
    val entityType = ModelSetEntity(None, ManualType("", "MyType"), None, Some(List()), None)
    val entity = EntityValue(None, None, Some(List()))
    val res = CheckEntityType.checkEntityType(entity, entityType).swap.toOption.get
    assert("TypeError" == res.head.code)
  }

  test("Wrong attribute") {
    val entityType = ModelSetEntity(None, ManualType("", "MyType"), None, Some(List(ModelSetAttribute(None, Some("attr"), ModelSetType(None, TLangString.getType)))), None)
    val entity = EntityValue(None, None, Some(List()))
    val res = CheckEntityType.checkEntityType(entity, entityType).swap.toOption.get
    assert("TypeError" == res.head.code)
  }

  //  test("Correct parameter") {
  //    val entityType = ModelSetEntity(None, "MyType", Some(List(ModelSetAttribute(None, Some("attr"), ModelSetType(None, TLangString.getType)))), None)
  //    val entity = EntityValue(None, None, Some(List(Model)), None)
  //    assert(CheckEntityType.checkEntityType(entity, entityType).isRight)
  //  }
}
