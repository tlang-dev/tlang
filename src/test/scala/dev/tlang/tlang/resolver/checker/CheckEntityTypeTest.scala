package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import org.scalatest.funsuite.AnyFunSuite

class CheckEntityTypeTest extends AnyFunSuite {

  test("Empty parameters") {
    val entityType = ModelSetEntity(None, "MyType", Some(List()), None)
    val entity = EntityValue(None, Some("MyType"), Some(List()), None)
    assert(CheckEntityType.checkEntityType(entity, entityType).isRight)
  }

  test("No type in entity") {
    val entityType = ModelSetEntity(None, "MyType", Some(List()), None)
    val entity = EntityValue(None, None, Some(List()), None)
    val res = CheckEntityType.checkEntityType(entity, entityType).swap.toOption.get
    assert("TypeError" == res.head.code)
  }

  test("Wrong parameter") {
    val entityType = ModelSetEntity(None, "MyType", Some(List(ModelSetAttribute(None, Some("attr"), ModelSetType(None, TLangString.getType)))), None)
    val entity = EntityValue(None, None, Some(List()), None)
    val res = CheckEntityType.checkEntityType(entity, entityType).swap.toOption.get
    assert("TypeError" == res.head.code)
  }

  test("Wrong attribute") {
    val entityType = ModelSetEntity(None, "MyType", None, Some(List(ModelSetAttribute(None, Some("attr"), ModelSetType(None, TLangString.getType)))))
    val entity = EntityValue(None, None, None, Some(List()))
    val res = CheckEntityType.checkEntityType(entity, entityType).swap.toOption.get
    assert("TypeError" == res.head.code)
  }

//  test("Correct parameter") {
//    val entityType = ModelSetEntity(None, "MyType", Some(List(ModelSetAttribute(None, Some("attr"), ModelSetType(None, TLangString.getType)))), None)
//    val entity = EntityValue(None, None, Some(List(Model)), None)
//    assert(CheckEntityType.checkEntityType(entity, entityType).isRight)
//  }
}
