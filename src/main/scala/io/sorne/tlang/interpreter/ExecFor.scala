package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.interpreter.context.Context

object ExecFor extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    Left(NotImplemented())
  }
}
