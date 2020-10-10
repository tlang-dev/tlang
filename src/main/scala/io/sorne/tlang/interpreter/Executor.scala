package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.HelperStatement

trait Executor {

  def run(statement: HelperStatement, context: Context): Either[ExecError, Option[Value[_]]]

}
