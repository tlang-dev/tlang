package io.sorne.tlang.libraries.generator

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.interpreter.context.Context
import io.sorne.tlang.interpreter.{ExecError, Executor, Value}

object Generator extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    Right(None)
  }
}
