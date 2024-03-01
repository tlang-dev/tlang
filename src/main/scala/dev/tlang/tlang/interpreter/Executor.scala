package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Value

trait Executor {

  def run(statement: HelperStatement, context: AstContext): Either[ExecError, Option[List[Value]]]

}
