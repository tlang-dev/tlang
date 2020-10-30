package io.sorne.tlang.libraries.generator

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.interpreter.{Context, ExecError, Executor, Value}

object Generator extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[Value[_]]] = {
    Right(None)
  }
}
