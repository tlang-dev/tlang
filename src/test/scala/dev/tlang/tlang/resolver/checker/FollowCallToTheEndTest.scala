package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallVarObject}
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper.{HelperContent, HelperFunc}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef, ModelSetType}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class FollowCallToTheEndTest extends AnyFunSuite {

  test("Call simple var") {
    val call = CallObject(None, List(CallVarObject(None, "var1")))
    val context = Context(List(Scope(models = mutable.Map("var1" -> ModelSetType(None, TLangString.getType)))))
    val elem = FollowCallToTheEnd.followCallToTheEnd(call, context).toOption.get.get
    assert(TLangString.getType == elem.asInstanceOf[ModelSetType].`type`)
  }

  test("Call var in set entity") {
    val call = CallObject(None, List(CallVarObject(None, "param1")))
    val context = Context(List(Scope()))
    val entity = ModelSetEntity(None, "myEntity", None, Some(List(ModelSetAttribute(None, Some("param1"), ModelSetType(None, TLangString.getType)))), None)
    val elem = FollowCallToTheEnd.followSetEntity(entity, call, context, 0).toOption.get.get
    assert(TLangString.getType == elem.asInstanceOf[ModelSetType].`type`)
  }

  test("Call set ref with func") {
    val call = CallObject(None, List(CallFuncObject(None, Some("myFunc"), None)))
    val ref = ModelSetRef(None, List("myFunc"), None)
    val func = HelperFunc(None, "myFunc", None, Some(List(ObjType(None, None, TLangString.getType))), HelperContent(None, None))
    val context = Context(List(Scope(functions = mutable.Map("myFunc" -> func))))
    val elem = FollowCallToTheEnd.followSetRef(ref, call, context, 0).toOption.get.get
    assert(TLangString.getType == elem.asInstanceOf[TLangString].getType)
  }

  test("Call func in set entity") {
    val call = CallObject(None, List(CallVarObject(None, "var1"), CallFuncObject(None, Some("param1"), None)))
    val entity = ModelSetEntity(None, "myEntity", None, Some(List(ModelSetAttribute(None, Some("param1"), ModelSetRef(None, List("myFunc"), None)))), None)
    val context = Context(List(Scope(
      functions = mutable.Map("myFunc" -> HelperFunc(None, "myFunc", None, Some(List(ObjType(None, None, TLangString.getType))), HelperContent(None, None))),
      models = mutable.Map("var1" -> entity))))
    val elem = FollowCallToTheEnd.followCallToTheEnd(call, context).toOption.get.get
    assert(TLangString.getType == elem.asInstanceOf[TLangString].getType)
  }

}
