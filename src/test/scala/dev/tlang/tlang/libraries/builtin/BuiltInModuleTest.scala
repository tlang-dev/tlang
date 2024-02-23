package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue, TLangBool, TLangString}
import dev.tlang.tlang.interpreter.ExecFunc
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite
import tlang.core.Null
import tlang.core.func.FuncRet
import tlang.std.entity.StdEntity

import scala.collection.mutable

class BuiltInModuleTest extends AnyFunSuite {

  test("Call method") {
    val module = BuiltInModule.buildModule(classOf[StdEntity])
    val printFunc = module.getFunctions.filter(_.name == "exists").head
    val entity = EntityValue(Null.empty(), None, Some(List(ComplexAttribute(Null.empty(), Some("attr1"), None, Operation(Null.empty(), None, Right(new TLangString(Null.empty(), "Just some random text")))))))

//    val res = ExecFunc.run(printFunc, Context(List(Scope(variables = mutable.Map(("arg1", entity), ("arg2", new TLangString(Null.empty(), "attr1")))))))
//    assert(res.toOption.get.get.head.asInstanceOf[FuncRet].get().head.get().asInstanceOf[TLangBool].getElement)

//    val res2 = ExecFunc.run(printFunc, Context(List(Scope(variables = mutable.Map(("arg1", entity), ("arg2", new TLangString(Null.empty(), "attr2")))))))
//    assert(!res2.toOption.get.get.head.asInstanceOf[FuncRet].get().head.get().asInstanceOf[TLangBool].getElement)
  }
}
