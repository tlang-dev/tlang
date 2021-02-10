package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.helper.{HelperContent, HelperStatement}
import dev.tlang.tlang.interpreter.context.Context

object ExecContent extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val content = statement.asInstanceOf[HelperContent]
    if (content.content.isDefined) {
      val statements = content.content.get
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
