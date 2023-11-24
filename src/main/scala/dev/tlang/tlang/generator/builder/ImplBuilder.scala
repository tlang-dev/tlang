package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.generator.builder.TemplateBuilder.includeTmplId
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.lang.ast.{TmplAnnotation, TmplID, TmplImpl}

import scala.collection.mutable.ListBuffer

object ImplBuilder {

  def buildImpl(impl: TmplImpl, context: Context): Either[ExecError, TmplImpl] = {
    if (impl.annots.isDefined) {
      val newAnnots = ListBuffer.empty[TmplAnnotation]
      impl.annots.get.foreach(annot => TemplateBuilder.buildAnnotation(annot, context) match {
        case Left(err) => Left(err)
        case Right(value) => newAnnots += value
      })
      impl.annots = Some(newAnnots.toList)
    }

    includeTmplId(impl.name, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        impl.name = value.head.asInstanceOf[TmplID]
        buildImplContent(impl, context)
    }
  }

  def buildImplContent(impl: TmplImpl, context: Context): Either[ExecError, TmplImpl] = {
    TemplateBuilder.buildContents(impl.content, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        impl.content = value
        Right(impl)
    }
  }

}
