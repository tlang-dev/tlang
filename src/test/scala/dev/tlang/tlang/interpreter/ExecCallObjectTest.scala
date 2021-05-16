package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{EntityValue, TLangString, _}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg, TmplStringID}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ExecCallObjectTest extends AnyFunSuite {

  test("Get simple variable") {
    val context = Context(List(Scope(variables = mutable.Map("var1" -> EntityValue(None, Some(ObjType(None, None, "MyEntity")))))))
    val statement = CallObject(None, List(CallVarObject(None, "var1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[EntityValue])
    assert("MyEntity".equals(res.head.asInstanceOf[EntityValue].`type`.get.getType))
  }

  test("Get variable from array by index") {
    val array = ArrayValue(None, Some(List(
      ComplexAttribute(None, value = Operation(None, None, Right(new TLangString(None, value = "value1")))),
      ComplexAttribute(None, value = Operation(None, None, Right(new TLangString(None, value = "value2")))),
      ComplexAttribute(None, value = Operation(None, None, Right(new TLangString(None, value = "value3"))))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array))))
    val statement = CallObject(None, List(CallArrayObject(None, "var1", Operation(None, None, Right(new TLangLong(None, 1))))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.asInstanceOf[Operation].content.toOption.get.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement))
  }

  test("Get variable from array by name") {
    val array = ArrayValue(None, Some(List(
      ComplexAttribute(None, Some("myPosition1"), value = Operation(None, None, Right(new TLangString(None, value = "value1")))),
      ComplexAttribute(None, Some("myPosition2"), value = Operation(None, None, Right(new TLangString(None, value = "value2")))),
      ComplexAttribute(None, Some("myPosition3"), value = Operation(None, None, Right(new TLangString(None, value = "value3"))))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array))))
    val statement = CallObject(None, List(CallArrayObject(None, "var1", Operation(None, None, Right(new TLangString(None, "myPosition2"))))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.asInstanceOf[Operation].content.toOption.get.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement))
  }

  test("Get variable from params in entity") {
    val myEntity = EntityValue(None, Some(ObjType(None, None, "MyEntity")), Some(List(
      ComplexAttribute(None, Some("attr1"), None, Operation(None, None, Right(new TLangString(None, value = "value1")))),
      ComplexAttribute(None, Some("attr2"), None, Operation(None, None, Right(new TLangString(None, value = "value2")))),
      ComplexAttribute(None, Some("attr3"), None, Operation(None, None, Right(new TLangString(None, value = "value3"))))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(None, List(CallVarObject(None, "var1"), CallVarObject(None, "attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[TLangString].getElement))
  }

  test("Get variable from attrs in entity") {
    val myEntity = EntityValue(None, Some(ObjType(None, None, "MyEntity")), Some(List(
      ComplexAttribute(None, Some("attr1"), None, Operation(None, None, Right(new TLangString(None, value = "value1")))),
      ComplexAttribute(None, Some("attr2"), None, Operation(None, None, Right(new TLangString(None, value = "value2")))),
      ComplexAttribute(None, Some("attr3"), None, Operation(None, None, Right(new TLangString(None, value = "value3"))))
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(None, List(CallVarObject(None, "var1"), CallVarObject(None, "attr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[TLangString].getElement))
  }

  test("Call function with one parameter") {
    val callInsideFunc = CallObject(None, List(CallVarObject(None, "valToReturn")))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(None, None, "String")))))), None, block = block)
    val caller = SetAttribute(None, value = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))
    val statement = CallObject(None, List(CallFuncObject(None, Some("myFunc"), Some(List(CallFuncParam(None, Some(List(caller))))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "MyValue")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getElement))
  }

  test("Call function with currying") {
    val callInsideFunc = MultiValue(None, List(
      CallObject(None, List(CallVarObject(None, "valToReturn"))),
      CallObject(None, List(CallVarObject(None, "valToReturn2")))))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myFunc", Some(List(
      HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(None, None, "String")))),
      HelperCurrying(None, List(HelperParam(None, Some("valToReturn2"), ObjType(None, None, "String")))))), None, block = block)
    val caller = SetAttribute(None, value = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))
    val caller2 = SetAttribute(None, value = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var2"))))))
    val statement = CallObject(None, List(CallFuncObject(None, Some("myFunc"), Some(List(
      CallFuncParam(None, Some(List(caller))),
      CallFuncParam(None, Some(List(caller2))),
    )))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "MyValue"), "var2" -> new TLangString(None, "MyValue2")), functions = mutable.Map("myFunc" -> funcDef))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getElement))
    assert("MyValue2".equals(res.last.asInstanceOf[TLangString].getElement))
  }

  test("Call function from entity") {
    val callInsideFunc = CallObject(None, List(CallVarObject(None, "valToReturn")))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(None, None, "String")))))), None, block = block)
    val caller = SetAttribute(None, value = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))
    val attrStatement = Operation(None, None, Right(CallObject(None, List(CallFuncObject(None, Some("myFunc"), Some(List(CallFuncParam(None, Some(List(caller))))))))))
    val myEntity = EntityValue(None, Some(ObjType(None, None, "MyEntity")), Some(List(
      ComplexAttribute(None, Some("attr1"), None, attrStatement),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "MyValue"), "myEntity" -> myEntity), functions = mutable.Map("myFunc" -> funcDef))))
    val statement = CallObject(None, List(CallVarObject(None, "myEntity"), CallVarObject(None, "attr1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getElement))
  }

  test("Call array from entity") {
    val array = ArrayValue(None, Some(List(
      ComplexAttribute(None, Some("myPosition1"), None, Operation(None, None, Right(new TLangString(None, value = "value1")))),
      ComplexAttribute(None, Some("myPosition2"), None, Operation(None, None, Right(new TLangString(None, value = "value2")))),
      ComplexAttribute(None, Some("myPosition3"), None, Operation(None, None, Right(new TLangString(None, value = "value3"))))
    )))
    val attrStatement = Operation(None, None, Right(CallObject(None, List(CallArrayObject(None, "var1", Operation(None, None, Right(new TLangString(None, "myPosition2"))))))))
    val myEntity = EntityValue(None, Some(ObjType(None, None, "MyEntity")), Some(List(
      ComplexAttribute(None, Some("attr1"), None, attrStatement),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> array, "myEntity" -> myEntity))))
    val statement = CallObject(None, List(CallVarObject(None, "myEntity"), CallVarObject(None, "attr1")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.asInstanceOf[Operation].content.toOption.get.isInstanceOf[TLangString])
    assert("value2".equals(res.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement))
  }

  test("Function called inside a function") {
    val callInsideFunc2 = CallObject(None, List(CallVarObject(None, "valToReturn")))
    val blockFunc2 = HelperContent(None, Some(List(callInsideFunc2)))
    val funcDef2 = HelperFunc(None, "myFunc2", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(None, None, "String")))))), None, block = blockFunc2)

    val caller = SetAttribute(None, value = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))
    val callInsideFunc1 = CallObject(None, List(CallFuncObject(None, Some("myFunc2"), Some(List(CallFuncParam(None, Some(List(caller))))))))
    val blockFunc1 = HelperContent(None, Some(List(callInsideFunc1)))
    val funcDef1 = HelperFunc(None, "myFunc1", None, Some(List(ObjType(None, None, "String"))), block = blockFunc1)

    val statement = CallObject(None, List(CallFuncObject(None, Some("myFunc1"), None)))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "MyValue")), functions = mutable.Map("myFunc2" -> funcDef2, "myFunc1" -> funcDef1))))
    val res = ExecCallObject.run(statement, context)
    val res2 = res.toOption.get.get
    assert("MyValue".equals(res2.head.asInstanceOf[TLangString].getElement))
  }

  test("Call function in other resources") {
    val callInsideFunc = CallObject(None, List(CallVarObject(None, "valToReturn")))
    val block = HelperContent(None, Some(List(callInsideFunc)))
    val funcDef = HelperFunc(None, "myFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(None, None, "String")))))), None, block = block)
    val caller = SetAttribute(None, value = Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "var1"))))))
    val statement = CallObject(None, List(CallVarObject(None, "myResource"), CallFuncObject(None, Some("myFunc"), Some(List(CallFuncParam(None, Some(List(caller))))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "MyValue")), functions = mutable.Map("myResource/myFunc" -> funcDef))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert("MyValue".equals(res.head.asInstanceOf[TLangString].getElement))
  }

  test("Call tmpl in other resources") {
    val tmpl = TmplBlock(None, "myTmpl", "scala", None, Some(new TmplPkg(List(TmplStringID(None, "pkg1")))), None)
    val statement = CallObject(None, List(CallVarObject(None, "myResource"), CallFuncObject(None, Some("myTmpl"), None)))
    val context = Context(List(Scope(templates = mutable.Map("myResource/myTmpl" -> tmpl))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TmplBlockAsValue])
    assert("pkg1" == res.head.asInstanceOf[TmplBlockAsValue].block.pkg.get.parts.head.toString)
  }

  test("Call attr in impl") {
    val myEntity = EntityValue(None, Some(ObjType(None, None, "MyEntity")), Some(List(
      ComplexAttribute(None, Some("attr1"), None, Operation(None, None, Right(new TLangString(None, value = "value1")))),
      ComplexAttribute(None, Some("attr2"), None, Operation(None, None, Right(EntityImpl(None, None, None,
        Some(List(
          ComplexAttribute(None, Some("implAttr1"), None, Operation(None, None, Right(new TLangString(None, value = "ImplVal1")))),
          ComplexAttribute(None, Some("implAttr2"), None, Operation(None, None, Right(new TLangString(None, value = "ImplVal2")))),
          ComplexAttribute(None, Some("implAttr3"), None, Operation(None, None, Right(new TLangString(None, value = "ImplVal3")))),
        )))))),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(None, List(CallVarObject(None, "var1"), CallVarObject(None, "attr2"), CallVarObject(None, "implAttr2")))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.isInstanceOf[TLangString])
    assert("ImplVal2" == res.head.asInstanceOf[TLangString].getElement)
  }

  test("Call array attr in impl") {
    val myEntity = EntityValue(None, Some(ObjType(None, None, "MyEntity")), Some(List(
      ComplexAttribute(None, Some("attr2"), None, Operation(None, None, Right(EntityImpl(None, None, None,
        Some(List(
          ComplexAttribute(None, Some("implAttr1"), None, Operation(None, None, Right(new ArrayValue(None, Some(List(
            ComplexAttribute(None, Some("elem1"), None, Operation(None, None, Right(new TLangString(None, value = "ValueElem1")))),
          )))))),
        )))))),
    )))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> myEntity))))
    val statement = CallObject(None, List(CallVarObject(None, "var1"), CallVarObject(None, "attr2"), CallArrayObject(None, "implAttr1", Operation(None, None, Right(new TLangString(None, "elem1"))))))
    val res = ExecCallObject.run(statement, context).toOption.get.get
    assert(res.head.asInstanceOf[Operation].content.toOption.get.isInstanceOf[TLangString])
    assert("ValueElem1" == res.head.asInstanceOf[Operation].content.toOption.get.asInstanceOf[TLangString].getElement)
  }

  test("Call ref func") {
    val block = HelperContent(None, None)
    val funcDef = HelperFunc(None, "myFunc", Some(List(HelperCurrying(None, List(HelperParam(None, Some("valToReturn"), ObjType(None, None, "String")))))), None, block = block)
    val refFunc = CallObject(None, List(CallRefFuncObject(None, Some("myFunc"), None, Some(Left(funcDef)))))
    val res = ExecCallObject.run(refFunc, Context()).toOption.get.get
    assert(res.head.isInstanceOf[CallRefFuncObject])
    assert("myFunc" == res.head.asInstanceOf[CallRefFuncObject].func.get.swap.toOption.get.name)
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
