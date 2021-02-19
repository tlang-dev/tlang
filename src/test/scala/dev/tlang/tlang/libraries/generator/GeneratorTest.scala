package dev.tlang.tlang.libraries.generator

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue, TmplPkg, TmplStringID}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.interpreter.{ElementNotFound, ExecCallFunc}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class GeneratorTest extends AnyFunSuite {

  test("Simple generation") {
    val block = TmplBlock("myBlock", "scala", None, Some(new TmplPkg(List(TmplStringID("myPackage")))))
    val blockAsValue = TmplBlockAsValue(block, Context())
    val res = Generator.generate(blockAsValue, Context()).toOption.get.getValue
    assert(res.contains("package myPackage"))
  }

  test("Not existing language") {
    val block = TmplBlock("myBlock", "klingon", None, Some(new TmplPkg(List(TmplStringID("myPackage")))))
    val blockAsValue = TmplBlockAsValue(block, Context())
    val res = Generator.generate(blockAsValue, Context()).left.toOption.get
    assert(res.isInstanceOf[ElementNotFound])
  }

  test("Call from function") {
    val block = TmplBlock("myBlock", "scala", None, Some(new TmplPkg(List(TmplStringID("myPackage")))))
    val blockAsValue = TmplBlockAsValue(block, Context())
    val context = Context(List(Scope(variables = mutable.Map("myTmpl" -> blockAsValue), functions = mutable.Map("generate" -> Generator.generateFunc))))
    val caller = CallFuncObject(Some("generate"), Some(List(CallFuncParam(Some(List(SetAttribute(value = CallObject(List(CallVarObject("myTmpl"))))))))))
    val res = ExecCallFunc.run(caller, context).toOption.get.get.head.asInstanceOf[TLangString].getValue
    assert(res.contains("package myPackage"))
  }

}