package dev.tlang.tlang.generator.builder

import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.Context

object FuncBuilder {

  def buildFunc(func: TmplFunc, context: Context): Either[ExecError, TmplFunc] = {
    if (func.content.isDefined) {
      TemplateBuilder.buildExpBlock(func.content.get, context) match {
        case Left(error) => Left(error)
        case Right(value) => func.content = Some(value)
          Right(func)
      }
    } else Right(func)
  }

}
