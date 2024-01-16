package dev.tlang.tlang.generator

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.generator.builder.TemplateBuilder
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.primitive.{LangArrayValue, LangStringValue}
import dev.tlang.tlang.tmpl.lang.ast.{LangInterpretedID, LangPkg, LangSetAttribute, LangStringID}
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class TemplateBuilderTest extends AnyFunSuite {

  //  test("Build pkg") {
  //    val context = Context()
  //    val block = TmplBlock(None, "block1", "scala", None, None, None, specialised = true, None, Scope())
  //    val blockAsValue = TmplBlockAsValue(None, block, context)
  //    TemplateBuilder.buildBlockAsValue(blockAsValue)
  //  }

  test("build Pkg") {
    val pkg = Some(LangPkg(None, List(LangStringID(None, "pkg1"))))
    val res = TemplateBuilder.buildPkg(pkg, Context()).toOption.get.get
    assert("pkg1" == res.parts.head.toString)
  }

  test("build Pkg with call") {
    val pkg = Some(LangPkg(None, List(LangStringID(None, "pkg1"), LangInterpretedID(None, Some("before_"), CallObject(None, List(CallVarObject(None, "var1"))), Some("_after")))))
    val res = TemplateBuilder.buildPkg(pkg, Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "during")))))).toOption.get.get
    assert("pkg1.before_during_after" == res.parts.mkString("."))
  }

 /* test("build Pkg with for in call") {
    val tmpl = TmplBlock(None, "myTmpl", "scala", Some(List(HelperParam(None, Some("index"), ObjType(None, None, "String")))), None, None, specialised = true, Some(List(
      TmplStringValue(None, TmplInterpretedID(None, Some("pkg"), CallObject(None, List(CallVarObject(None, "index"))), None))
    )))

    val pkg = Some(TmplPkg(None, List(TmplStringID(None, "pkg1"), TmplInterpretedID(None, None, CallObject(None, List(CallFuncObject(None, Some("forEach"),
      Some(List(CallFuncParam(None, Some(List(
        SetAttribute(None, None, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "array1")))))),
        SetAttribute(None, None, Operation(None, None, Right(CallObject(None, List(CallRefFuncObject(None, Some("myTmpl"), Some(List(CallFuncParam(None, Some(List(
          SetAttribute(None, None, Operation(None, None, Right(LazyValue(None, None, Some(TLangString))))),
        ))))), Some(Right(tmpl)))))))),
      )))))))), None), TmplStringID(None, "pkg5"))))

    val context = Context(List(Scope(variables = mutable.Map("array1" -> new ArrayValue(None, Some(List(
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "2")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "3")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "4")))),
    )))),
      functions = mutable.Map("forEach" -> BuiltIntLibs.buildIntLibs("forEach")))))
    val res = TemplateBuilder.buildPkg(pkg, context).toOption.get.get
    assert("pkg1.pkg2.pkg3.pkg4.pkg5" == res.parts.mkString("."))
  }

  test("Entity with for in call") {
    val tmpl = TmplBlock(None, "myTmpl", "scala", Some(List(HelperParam(None, Some("index"), ObjType(None, None, "String")))), None, None, specialised = true, Some(List(
      TmplAttribute(None, None, None, TmplOperation(None, Right(TmplStringValue(None, TmplInterpretedID(None, Some("myValue"), CallObject(None, List(CallVarObject(None, "index"))), None)))))
    )))

    val entity = TmplEntityValue(None, Some(TmplStringID(None, "myEntity")), Some(List(
      TmplAttribute(None, None, None, TmplOperation(None, Right(TmplStringValue(None, TmplStringID(None, "myValue1"))))),
      TmplInclude(None, List(CallObject(None, List(CallFuncObject(None, Some("forEach"),
        Some(List(CallFuncParam(None, Some(List(
          SetAttribute(None, None, Operation(None, None, Right(CallObject(None, List(CallVarObject(None, "array1")))))),
          SetAttribute(None, None, Operation(None, None, Right(CallObject(None, List(CallRefFuncObject(None, Some("myTmpl"), Some(List(CallFuncParam(None, Some(List(
            SetAttribute(None, None, Operation(None, None, Right(LazyValue(None, None, Some(TLangString))))),
          ))))), Some(Right(tmpl)))))))),
        )))))))))),
      TmplAttribute(None, None, None, TmplOperation(None, Right(TmplStringValue(None, TmplStringID(None, "myValue5"))))),
    )), None)

    val context = Context(List(Scope(variables = mutable.Map("array1" -> new ArrayValue(None, Some(List(
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "2")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "3")))),
      ComplexAttribute(None, None, None, Operation(None, None, Right(new TLangString(None, "4")))),
    )))),
      functions = mutable.Map("forEach" -> BuiltIntLibs.buildIntLibs("forEach")))))

    val res = EntityBuilder.buildEntity(entity, context).toOption.get
    assert("myValue1" == res.params.get.head.asInstanceOf[TmplAttribute].value.content.toOption.get.asInstanceOf[TmplStringValue].toString)
    assert("myValue2" == res.params.get(1).asInstanceOf[TmplAttribute].value.content.toOption.get.asInstanceOf[TmplStringValue].toString)
    assert("myValue3" == res.params.get(2).asInstanceOf[TmplAttribute].value.content.toOption.get.asInstanceOf[TmplStringValue].toString)
    assert("myValue4" == res.params.get(3).asInstanceOf[TmplAttribute].value.content.toOption.get.asInstanceOf[TmplStringValue].toString)
    assert("myValue5" == res.params.get.last.asInstanceOf[TmplAttribute].value.content.toOption.get.asInstanceOf[TmplStringValue].toString)
  }*/

  test("Build array with call") {
    val array = LangArrayValue(None, None, Some(List(LangSetAttribute(None, None, LangOperation(None, Right(LangStringValue(None, LangInterpretedID(None, Some("before_"), CallObject(None, List(CallVarObject(None, "var1"))), Some("_after")))))))))
    val context = Context(List(Scope(variables = mutable.Map("var1" -> new TLangString(None, "during")))))
    val res = TemplateBuilder.buildArray(array, context).toOption.get.params.get
    assert("before_during_after" == res.head.asInstanceOf[LangSetAttribute].value.content.toOption.get.toString)
  }
}
