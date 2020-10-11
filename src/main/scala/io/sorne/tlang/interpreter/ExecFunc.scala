package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperFunc, HelperStatement}

object ExecFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[Value[_]]] = {
    val funcStatement = statement.asInstanceOf[HelperFunc]
    if (funcStatement.block.content.isDefined) {
      val statements = funcStatement.block.content.get
      if (statements.size > 1) {
        for (i <- 0 to statements.size - 2) {
          ExecStatement.run(statements(i), context)
        }
      }
      ExecStatement.run(statements.last, context) match {
        case Left(value) => Left(value)
        case Right(value) => Right(value)
      }
    } else Right(None)
  }

}
