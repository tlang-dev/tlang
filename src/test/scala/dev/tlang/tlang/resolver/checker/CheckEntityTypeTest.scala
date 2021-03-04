package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import org.scalatest.funsuite.AnyFunSuite

class CheckEntityTypeTest extends AnyFunSuite {

  test("Test right parameters") {
    val entityType = ModelSetEntity(None, "MyType", Some(List()), None)
    val entity = EntityValue(None, Some("MyType"), Some(List()), None)
    assert(CheckEntityType.checkEntityType(entity, entityType).isRight)
  }

  test("No type in entity"){
    val entityType = ModelSetEntity(None, "MyType", Some(List()), None)
    val entity = EntityValue(None, None, Some(List()), None)
    val res = CheckEntityType.checkEntityType(entity, entityType).swap.toOption.get
    assert("TypeError" == res.head.code)
  }

}
