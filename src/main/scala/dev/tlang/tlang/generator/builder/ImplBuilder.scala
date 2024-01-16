package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.generator.builder.TemplateBuilder.includeTmplId
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.lang.ast.{LangAnnotation, LangID, LangImpl}

import scala.collection.mutable.ListBuffer

object ImplBuilder {

  def buildImpl(impl: LangImpl, context: Context): Either[ExecError, LangImpl] = {
    if (impl.annots.isDefined) {
      val newAnnots = ListBuffer.empty[LangAnnotation]
      impl.annots.get.foreach(annot => TemplateBuilder.buildAnnotation(annot, context) match {
        case Left(err) => Left(err)
        case Right(value) => newAnnots += value
      })
      impl.annots = Some(newAnnots.toList)
    }

    includeTmplId(impl.name, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        impl.name = value.head.asInstanceOf[LangID]
        buildImplContent(impl, context)
    }
  }

  def buildImplContent(impl: LangImpl, context: Context): Either[ExecError, LangImpl] = {
    TemplateBuilder.buildContents(impl.content, context) match {
      case Left(error) => Left(error)
      case Right(value) =>
        impl.content = value
        Right(impl)
    }
  }

}
