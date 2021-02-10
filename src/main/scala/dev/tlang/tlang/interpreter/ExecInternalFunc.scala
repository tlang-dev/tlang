package dev.tlang.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperInternalFunc, HelperStatement}
import io.sorne.tlang.interpreter.context.Context

object ExecInternalFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement.asInstanceOf[HelperInternalFunc].func(context)
  }
}
