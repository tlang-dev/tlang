package dev.tlang.tlang.libraries.generator

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.generator.builder.TemplateBuilder
import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import dev.tlang.tlang.tmpl.lang.ast.LangBlockAsValue
import tlang.core.Null

import scala.collection.mutable

object Generator {

  def generateFunc: HelperFunc = HelperFunc(Null.empty(), "generate", Some(List(HelperCurrying(Null.empty(), List(HelperParam(Null.empty(), Some("block"), ObjType(Null.empty(), None, LangBlockAsValue.getType)))))),
    Null.of(List(ObjType(Null.empty(), None, TLangString.getType))), HelperContent(Null.empty(), Some(List(
      HelperInternalFunc((context: Context) => {
        ContextUtils.findVar(context, "block") match {
          case Some(block) => generate(block.asInstanceOf[LangBlockAsValue], context) match {
            case Left(error) =>
              Right(Some(List(new TLangString(Null.empty(), error.toString))))
            case Right(value) => Right(Some(List(value)))
          }
          case None => Right(Some(List(new TLangString(Null.empty(), ""))))
        }
      })
    ))))

  def generate(block: LangBlockAsValue, context: Context): Either[ExecError, TLangString] = {
    TemplateBuilder.buildBlockAsValue(block) match {
      case Left(error) => Left(error)
      case Right(newBlock) => generateAfterMapping(newBlock, context)
    }
  }

  private def generateAfterMapping(block: LangBlockAsValue, context: Context): Either[ExecError, TLangString] = {
    //    generators.get(block.block.lang) match {
    //      case None => Left(ElementNotFound("This language does not exist: " + block.block.lang))
    //      case Some(generator) =>
    //        TemplateBuilder.buildBlockAsValue(block) match {
    //          case Left(error) => Left(error)
    //          case Right(newBlock) => Right(new TLangString(None, generator.generate(newBlock.block)))
    //        }
    //    }
    val newScope = Scope(variables = mutable.Map("code" -> block.block.toEntity))
    val newContext = Context(context.scopes :+ newScope)

    ContextUtils.findFunc(block.context, block.block.getLangs.head) match {
      case Some(func) => ExecFunc.run(func, newContext) match {
        case Left(error) => Left(error)
        case Right(results) =>
          if (results.isDefined && results.get.nonEmpty && results.get.head.isInstanceOf[TLangString])
            Right(results.get.head.asInstanceOf[TLangString])
          else Left(NoValue("No value or wrong value returned when generating the language", block.block.getContext))
      }
      case None => Left(ElementNotFound("Could not find a generator for this language: " + block.block.getLangs.head, block.block.getContext))
    }
  }

}
