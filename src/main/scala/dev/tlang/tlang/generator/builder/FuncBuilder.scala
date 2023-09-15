package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.ast.tmpl.TmplAnnotation
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.Context

import scala.collection.mutable.ListBuffer

object FuncBuilder {

  def buildFunc(func: TmplFunc, context: Context): Either[ExecError, TmplFunc] = {
    if (func.annots.isDefined) {
      val newAnnots = ListBuffer.empty[TmplAnnotation]
      func.annots.get.foreach(annot => TemplateBuilder.buildAnnotation(annot, context) match {
        case Left(err) => Left(err)
        case Right(value) => newAnnots += value
      })
      func.annots = Some(newAnnots.toList)
    }
    if (func.content.isDefined) {
      TemplateBuilder.buildExpContent(func.content.get, context) match {
        case Left(error) => Left(error)
        case Right(value) => func.content = Some(value)
          Right(func)
      }
    } else Right(func)
  }

}
