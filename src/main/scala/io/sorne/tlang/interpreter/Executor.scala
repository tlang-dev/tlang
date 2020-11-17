package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.interpreter.context.Context

trait Executor {

  def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]]

}
