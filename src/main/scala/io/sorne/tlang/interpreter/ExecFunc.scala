package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperFunc, HelperStatement}
import io.sorne.tlang.interpreter.context.Context

object ExecFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val funcStatement = statement.asInstanceOf[HelperFunc]
    if (funcStatement.block.content.isDefined) {
      ExecContent.run(funcStatement.block, context)
    } else Right(None)
  }

}
