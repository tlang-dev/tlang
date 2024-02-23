package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value.{EntityValue, LazyValue, TLangString}
import dev.tlang.tlang.ast.helper.{HelperContent, HelperCurrying, HelperFunc, HelperParam}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef, ModelSetRefCurrying}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallObjectInModelTest extends AnyFunSuite {

 /* test("Call var in model") {
    val scope = Scope(variables = mutable.Map("myOtherVar" -> new TLangString(None, "myValue")))
    val model = ModelSetEntity(None, "MyModel", None, None, Some(List(ModelSetAttribute(None, Some("myVar"), ModelSetRef(None, List("myOtherVar"), None, None, scope)))))
    val entity = EntityValue(None, Some(ObjType(None, None, "MyModel")), None, Scope(models = mutable.Map("MyModel" -> model)))
    val caller = CallObject(None, List(CallVarObject(None, "myEntity"), CallVarObject(None, "myVar")))
    val context = Context(List(Scope(variables = mutable.Map("myEntity" -> entity))))
    val res = ExecCallObject.run(caller, context).toOption.get.get.head.asInstanceOf[TLangString].getElement
    assert("myValue" == res)
  }

  test("Call func in model by var") {
    val callInsideFunc = CallObject(None, List(CallVarObject(None, "valToReturn")))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myOtherFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(None, None, "String")))))), None, block = block)
    val scope = Scope(variables=mutable.Map("var1" -> new TLangString(None, "myValue")),functions = mutable.Map("myOtherFunc" -> funcDef))
    val model = ModelSetEntity(None, "MyModel", None, None, Some(List(ModelSetAttribute(None, Some("myFunc"), ModelSetRef(None, List("myOtherFunc"), Some(List(ModelSetRefCurrying(None, List(Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))))), Some(Left(funcDef)),scope)))))
    val entity = EntityValue(None, Some(ObjType(None, None, "MyModel")), None, Scope(models = mutable.Map("MyModel" -> model)))
    val caller = CallObject(None, List(CallVarObject(None, "myEntity"), CallVarObject(None, "myFunc")))
    val context = Context(List(Scope(variables = mutable.Map("myEntity" -> entity))))
    val res = ExecCallObject.run(caller, context).toOption.get.get.head.asInstanceOf[TLangString].getElement
    assert("myValue" == res)
  }

  test("Call func in model by func caller") {
    val callInsideFunc = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "param1")))), Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "param2"))))))))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myOtherFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("param1"), ObjType(None, None, "String")), HelperParam(None, Some("param2"), ObjType(None, None, "String")))))), None, block = block)
    val scope = Scope(functions = mutable.Map("myOtherFunc" -> funcDef))
    val refScope = Scope(variables = mutable.Map("var1" -> new TLangString(None, "world!")))
    val model = ModelSetEntity(None, "MyModel", None, None, Some(List(ModelSetAttribute(None, Some("myFunc"), ModelSetRef(None, List("myOtherFunc"), Some(List(ModelSetRefCurrying(None, List(Operation(None, None, Right(LazyValue(None, None, None))), Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))))), Some(Left(funcDef)), refScope)))), scope)
    val entity = EntityValue(None, Some(ObjType(None, None, "MyModel")), None, Scope(models = mutable.Map("MyModel" -> model)))
    val caller = CallObject(None, List(CallVarObject(None, "myEntity"), CallFuncObject(None, Some("myFunc"), Some(List(CallFuncParam(None, Some(List(SetAttribute(None, None, Operation(None, None, Right(new TLangString(None, "Hello, "))))))))))))
    val context = Context(List(Scope(variables = mutable.Map("myEntity" -> entity))))
    val res = ExecCallObject.run(caller, context).toOption.get.get.head.asInstanceOf[TLangString].getElement
    assert("Hello, world!" == res)
  }

  test("Call func in model extending another model by func caller") {
    val callInsideFunc = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "param1")))), Some((Operator.ADD, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "param2"))))))))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myOtherFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("param1"), ObjType(None, None, "String")), HelperParam(None, Some("param2"), ObjType(None, None, "String")))))), None, block = block)
    val topModelScope = Scope(functions = mutable.Map("myOtherFunc" -> funcDef))
    val refScope = Scope(variables = mutable.Map("var1" -> new TLangString(None, "world!")))
    val topModel = ModelSetEntity(None, "MyTopModel", None,  None, Some(List(ModelSetAttribute(None, Some("myFunc"), ModelSetRef(None, List("myOtherFunc"), Some(List(ModelSetRefCurrying(None, List(Operation(None, None, Right(LazyValue(None, None, None))), Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))))), Some(Left(funcDef)), refScope)))), topModelScope)
    val modelScope = Scope(models = mutable.Map("MyTopModel" -> topModel))
    val model = ModelSetEntity(None, "MyModel", Some(ObjType(None, None, "MyTopModel")), None, None, modelScope)
    val entity = EntityValue(None, Some(ObjType(None, None, "MyModel")), None, Scope(models = mutable.Map("MyModel" -> model)))
    val caller = CallObject(None, List(CallVarObject(None, "myEntity"), CallFuncObject(None, Some("myFunc"), Some(List(CallFuncParam(None, Some(List(SetAttribute(None, None, Operation(None, None, Right(new TLangString(None, "Hello, "))))))))))))
    val context = Context(List(Scope(variables = mutable.Map("myEntity" -> entity))))
    val res = ExecCallObject.run(caller, context).toOption.get.get.head.asInstanceOf[TLangString].getElement
    assert("Hello, world!" == res)
  }*/

}
