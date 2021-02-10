package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context

trait Executor {

  def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]]

}
