package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperIf, HelperStatement}
import io.sorne.tlang.interpreter.`type`.Bool

object ExecIf extends Executor {
  override def run(statement: HelperStatement, context: Context): Option[Value[_]] = {
    val ifStatement = statement.asInstanceOf[HelperIf]
    ExecStatement.run(ifStatement.statement1, context) match {
      case Some(value) => {
        value match {
          case bool: Bool if ifStatement.condition.isEmpty => Some(new Bool(bool.getValue))
          case _ => None
        }
      }
      case None => None
    }
    None
  }
}
