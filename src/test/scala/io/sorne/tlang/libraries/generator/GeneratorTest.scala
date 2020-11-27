package io.sorne.tlang.libraries.generator

import io.sorne.tlang.ast.helper.call.{HelperCallFuncObject, HelperCallFuncParam, HelperCallObject, HelperCallVarObject}
import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import io.sorne.tlang.interpreter.{ElementNotFound, ExecCallFunc, ExecFunc}
import io.sorne.tlang.interpreter.`type`.TLangString
import io.sorne.tlang.interpreter.context.{Context, Scope}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class GeneratorTest extends AnyFunSuite {

  test("Simple generation") {
    val block = TmplBlock("myBlock", "scala", None, Some("myPackage"))
    val blockAsValue = TmplBlockAsValue(block, List())
    val res = Generator.generate(blockAsValue, Context()).toOption.get.getValue
    assert(res.contains("package myPackage"))
  }

  test("Not existing language") {
    val block = TmplBlock("myBlock", "klingon", None, Some("myPackage"))
    val blockAsValue = TmplBlockAsValue(block, List())
    val res = Generator.generate(blockAsValue, Context()).left.toOption.get
    assert(res.isInstanceOf[ElementNotFound])
  }

  test("Call from function") {
    val block = TmplBlock("myBlock", "scala", None, Some("myPackage"))
    val blockAsValue = TmplBlockAsValue(block, List())
    val context = Context(List(Scope(variables = mutable.Map("myTmpl" -> blockAsValue), functions = mutable.Map("generate" -> Generator.generateFunc))))
    val caller = HelperCallFuncObject(Some("generate"), Some(List(HelperCallFuncParam(List(HelperCallObject(List(HelperCallVarObject("myTmpl"))))))))
    val res = ExecCallFunc.run(caller, context).toOption.get.get.head.asInstanceOf[TLangString].getValue
    assert(res.contains("package myPackage"))
  }

}
