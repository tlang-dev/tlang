package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement

object ExecFor extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[Value[_]]] = {
    Left(NotImplemented())
  }
}
