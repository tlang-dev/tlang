package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue, TLangString}
import org.scalatest.funsuite.AnyFunSuite
import tlang.std.entity.StdEntity

class BuiltInModuleTest extends AnyFunSuite {

  test("Call method") {
    val module = BuiltInModule.buildModule(classOf[StdEntity])
    val printFunc = module.getFunctions.filter(_.name == "exists").head
    val entity = EntityValue(None, None, None, Some(List(ComplexAttribute(None, Some("attr1"), None, Operation(None, None, Right(new TLangString(None, "Just some random text")))))))

    //    val res = ExecFunc.run(printFunc, Context(List(Scope(variables = mutable.Map(("arg1", entity), ("arg2", new TLangString(None, "attr1")))))))
    //    assert(res.toOption.get.get.head.asInstanceOf[FuncRet].get().head.get().asInstanceOf[TLangBool].getElement)

    //    val res2 = ExecFunc.run(printFunc, Context(List(Scope(variables = mutable.Map(("arg1", entity), ("arg2", new TLangString(None, "attr2")))))))
    //    assert(!res2.toOption.get.get.head.asInstanceOf[FuncRet].get().head.get().asInstanceOf[TLangBool].getElement)
  }
}
