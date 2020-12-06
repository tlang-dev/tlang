package io.sorne.tlang.libraries.generator

import io.sorne.tlang.ast.common.call._
import io.sorne.tlang.ast.common.value.TLangString
import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg}
import io.sorne.tlang.interpreter.context.{Context, Scope}
import io.sorne.tlang.interpreter.{ElementNotFound, ExecCallFunc}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class GeneratorTest extends AnyFunSuite {

  test("Simple generation") {
    val block = TmplBlock("myBlock", "scala", None, Some(new TmplPkg(List("myPackage"))))
    val blockAsValue = TmplBlockAsValue(block, Map())
    val res = Generator.generate(blockAsValue, Context()).toOption.get.getValue
    assert(res.contains("package myPackage"))
  }

  test("Not existing language") {
    val block = TmplBlock("myBlock", "klingon", None, Some(new TmplPkg(List("myPackage"))))
    val blockAsValue = TmplBlockAsValue(block, Map())
    val res = Generator.generate(blockAsValue, Context()).left.toOption.get
    assert(res.isInstanceOf[ElementNotFound])
  }

  test("Call from function") {
    val block = TmplBlock("myBlock", "scala", None, Some(new TmplPkg(List("myPackage"))))
    val blockAsValue = TmplBlockAsValue(block, Map())
    val context = Context(List(Scope(variables = mutable.Map("myTmpl" -> blockAsValue), functions = mutable.Map("generate" -> Generator.generateFunc))))
    val caller = CallFuncObject(Some("generate"), Some(List(CallFuncParam(Some(List(SetAttribute(value = CallObject(List(CallVarObject("myTmpl"))))))))))
    val res = ExecCallFunc.run(caller, context).toOption.get.get.head.asInstanceOf[TLangString].getValue
    assert(res.contains("package myPackage"))
  }

}
