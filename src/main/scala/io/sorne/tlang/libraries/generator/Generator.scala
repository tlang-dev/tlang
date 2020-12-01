package io.sorne.tlang.libraries.generator

import io.sorne.tlang.ast.common.value.TLangString
import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.tmpl.TmplBlockAsValue
import io.sorne.tlang.generator.CodeGenerator
import io.sorne.tlang.generator.groovy.GroovyGenerator
import io.sorne.tlang.generator.java.JavaGenerator
import io.sorne.tlang.generator.json.JSONGenerator
import io.sorne.tlang.generator.rust.RustGenerator
import io.sorne.tlang.generator.scala.ScalaGenerator
import io.sorne.tlang.generator.xml.XMLGenerator
import io.sorne.tlang.generator.yml.YMLGenerator
import io.sorne.tlang.interpreter._
import io.sorne.tlang.interpreter.context.{Context, ContextUtils}

object Generator {

  val generators: Map[String, CodeGenerator] = Map(
    "scala" -> new ScalaGenerator(),
    "java" -> new JavaGenerator(),
    "groovy" -> new GroovyGenerator(),
    "json" -> new JSONGenerator(),
    "rust" -> new RustGenerator(),
    "xml" -> new XMLGenerator(),
    "yml" -> new YMLGenerator(),
  )

  def generateFunc: HelperFunc = HelperFunc("generate", Some(List(HelperCurrying(List(HelperParam(Some("block"), HelperObjType(TmplBlockAsValue.getType)))))),
    Some(List(HelperObjType(TLangString.getType))), HelperContent(Some(List(
      HelperInternalFunc((context: Context) => {
        ContextUtils.findVar(context, "block") match {
          case Some(block) => generate(block.asInstanceOf[TmplBlockAsValue], context) match {
            case Left(_) => Right(Some(List(new TLangString(""))))
            case Right(value) => Right(Some(List(value)))
          }
          case None => Right(Some(List(new TLangString(""))))
        }
      })
    ))))

  def generate(block: TmplBlockAsValue, context: Context): Either[ExecError, TLangString] = {
    generators.get(block.block.lang) match {
      case None => Left(ElementNotFound("This language does not exist: " + block.block.lang))
      case Some(generator) => Right(new TLangString(generator.generate(block.block)))
    }
  }

}
