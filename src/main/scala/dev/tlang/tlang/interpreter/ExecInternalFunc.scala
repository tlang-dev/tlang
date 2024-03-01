package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.helper.{HelperInternalFunc, HelperStatement}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Value

object ExecInternalFunc extends Executor {
  override def run(statement: HelperStatement, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    val funcStatement = statement.asInstanceOf[HelperInternalFunc]
    //    val newContext = Context(context.scopes :+ funcStatement.func.)
    statement.asInstanceOf[HelperInternalFunc].func(context)
  }
}
