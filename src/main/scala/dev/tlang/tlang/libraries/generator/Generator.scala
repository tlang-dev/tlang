package dev.tlang.tlang.libraries.generator

import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.tmpl.TmplBlockAsValue
import dev.tlang.tlang.generator.dart.DartGenerator
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
    "dart" -> new DartGenerator(),
    "groovy" -> new GroovyGenerator(),
    "json" -> new JSONGenerator(),
    "rust" -> new RustGenerator(),
    "xml" -> new XMLGenerator(),
    "yml" -> new YMLGenerator(),
  )

  def generateFunc: HelperFunc = HelperFunc(None, "generate", Some(List(HelperCurrying(None, List(HelperParam(None, Some("block"), HelperObjType(None, TmplBlockAsValue.getType)))))),
    Some(List(HelperObjType(None, TLangString.getType))), HelperContent(None, Some(List(
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
        val newBlock = ValueMapper.mapBlock(block)
        Right(new TLangString(None, generator.generate(newBlock.block)))
    }
  }

}
