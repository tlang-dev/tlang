package dev.tlang.tlang.generator

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, TLangString}
import dev.tlang.tlang.ast.helper.{HelperObjType, HelperParam}
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.primitive.TmplStringValue
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.libraries.builtin.BuiltIntLibs
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class TemplateBuilderTest extends AnyFunSuite {

  test("Build pkg") {
    val context = Context()
    val block = TmplBlock(None, "block1", "scala", None, None, None, None, None, Scope())
    val blockAsValue = TmplBlockAsValue(None, block, context)
    TemplateBuilder.buildBlockAsValue(blockAsValue)
  }

  test("build Pkg") {
    val pkg = Some(new TmplPkg(List(TmplStringID(None, "pkg1"))))
    val res = TemplateBuilder.buildPkg(pkg, Context()).toOption.get.get
    assert("pkg1" == res.parts.head.toString)
  }

  test("build Pkg with call") {
    val pkg = Some(new TmplPkg(List(TmplStringID(None, "pkg1"), TmplInterpretedID(None, Some("before_"), CallObject(None, List(CallVarObject(None, "var1"))), Some("_after")))))
    val res = TemplateBuilder.buildPkg(pkg, Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "during")))))).toOption.get.get
    assert("pkg1.before_during_after" == res.parts.mkString("."))
  }

  test("build Pkg with for in call") {
    val tmpl = TmplBlock(None, "myTmpl", "scala", Some(List(HelperParam(None, Some("index"), HelperObjType(None, "String")))), None, None, Some("String"), Some(List(
      TmplStringValue(None, TmplInterpretedID(None, Some("pkg"), CallObject(None, List(CallVarObject(None, "index"))), None))
    )))

    val pkg = Some(new TmplPkg(List(TmplStringID(None, "pkg1"), TmplInterpretedID(None, None, CallObject(None, List(CallFuncObject(None, Some("forEach"),
      Some(List(CallFuncParam(None, Some(List(
        SetAttribute(None, None, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "array1")))))),
        SetAttribute(None, None, Operation(None, None, Right(CallObject(None, List(CallRefFuncObject(None, Some("myTmpl"), Some(List(CallFuncParam(None, Some(List(
          SetAttribute(None, None, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "_")))))),
        ))))), Some(Right(tmpl)))))))),
      )))))))), None), TmplStringID(None, "pkg5"))))

    val context = Context(List(Scope(variables = mutable.Map("array1" -> new ArrayValue(None, Some(List(
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "2")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "3")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "4")))),
    )))),
      functions = mutable.Map("forEach" -> BuiltIntLibs.buildIntLibs("forEach")))))
    val res = TemplateBuilder.buildPkg(pkg, context).toOption.get.get
//    val res = ValueMapper.mapPkg(TemplateBuilder.buildPkg(pkg, context).toOption.get, context).get
    assert("pkg1.pkg2.pkg3.pkg4.pkg5" == res.parts.mkString("."))
  }
}
