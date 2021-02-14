package dev.tlang.tlang.libraries.generator

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.TmplBlockAsValue
import dev.tlang.tlang.generator.groovy.GroovyGenerator
import dev.tlang.tlang.generator.java.JavaGenerator
import dev.tlang.tlang.generator.json.JSONGenerator
import dev.tlang.tlang.generator.rust.RustGenerator
import dev.tlang.tlang.generator.scalalang.ScalaGenerator
import dev.tlang.tlang.generator.typescript.TypeScriptGenerator
import dev.tlang.tlang.generator.xml.XMLGenerator
import dev.tlang.tlang.generator.yml.YMLGenerator
import dev.tlang.tlang.generator.{CodeGenerator, ValueMapper}
import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}

object Generator {

  val generators: Map[String, CodeGenerator] = Map(
    "scala" -> new ScalaGenerator(),
    "java" -> new JavaGenerator(),
    "typescript" -> new TypeScriptGenerator(),
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
      case Some(generator) =>
        val newBlock = ValueMapper.map(block)
        Right(new TLangString(generator.generate(newBlock.block)))
    }
  }

}
