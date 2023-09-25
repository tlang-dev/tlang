package dev.tlang.tlang.libraries.generator

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.TmplBlockAsValue
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.builder.TemplateBuilder
import dev.tlang.tlang.generator.langs.dart.DartGeneratorGen3
import dev.tlang.tlang.generator.langs.groovy.GroovyGenerator
import dev.tlang.tlang.generator.langs.json.JSONGenerator
import dev.tlang.tlang.generator.langs.kotlin.KotlinGenerator
import dev.tlang.tlang.generator.langs.rust.RustGenerator
import dev.tlang.tlang.generator.langs.xml.XMLGenerator
import dev.tlang.tlang.generator.langs.yml.YMLGenerator
import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}

object Generator {

  val generators: Map[String, CodeGenerator] = Map(
//    "scala" -> new ScalaGenerator(),
//    "java" -> new JavaGenerator(),
//    "typescript" -> new TypeScriptGenerator(),
    "dart" -> new DartGeneratorGen3(),
    "groovy" -> new GroovyGenerator(),
    "json" -> new JSONGenerator(),
    "rust" -> new RustGenerator(),
    "xml" -> new XMLGenerator(),
    "yml" -> new YMLGenerator(),
    "kotlin" -> new KotlinGenerator(),
  )

  def generateFunc: HelperFunc = HelperFunc(None, "generate", Some(List(HelperCurrying(None, List(HelperParam(None, Some("block"), ObjType(None, None, TmplBlockAsValue.getType)))))),
    Some(List(ObjType(None, None, TLangString.getType))), HelperContent(None, Some(List(
      HelperInternalFunc((context: Context) => {
        ContextUtils.findVar(context, "block") match {
          case Some(block) => generate(block.asInstanceOf[TmplBlockAsValue], context) match {
            case Left(_) => Right(Some(List(new TLangString(None, ""))))
            case Right(value) => Right(Some(List(value)))
          }
          case None => Right(Some(List(new TLangString(None, ""))))
        }
      })
    ))))

  def generate(block: TmplBlockAsValue, context: Context): Either[ExecError, TLangString] = {
    generators.get(block.block.lang) match {
      case None => Left(ElementNotFound("This language does not exist: " + block.block.lang))
      case Some(generator) =>
        TemplateBuilder.buildBlockAsValue(block) match {
          case Left(error) => Left(error)
          case Right(newBlock) => Right(new TLangString(None, generator.generate(newBlock.block)))
        }
    }
  }

}
