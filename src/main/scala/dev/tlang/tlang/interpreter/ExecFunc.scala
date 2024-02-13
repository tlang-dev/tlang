package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.interpreter.context.Context
import tlang.core.Value

object ExecFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val funcStatement = statement.asInstanceOf[HelperFunc]
    val newContext = Context(context.scopes :+ funcStatement.scope)
    if (funcStatement.block.content.isDefined) {
      ExecContent.run(funcStatement.block, newContext)
    } else Right(None)
  }

}
