package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.ast.tmpl.{TmplID, TmplImpl}
import dev.tlang.tlang.generator.builder.TemplateBuilder.includeTmplId
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.Context

object ImplBuilder {

  def buildImpl(impl: TmplImpl, context: Context): Either[ExecError, TmplImpl] = {
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
