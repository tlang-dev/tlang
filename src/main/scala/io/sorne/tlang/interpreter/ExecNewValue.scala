package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperNewValue, HelperStatement}
import io.sorne.tlang.ast.model.let.ModelLetMultiValue
import io.sorne.tlang.interpreter.context.Context

object ExecNewValue extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val newStatement = statement.asInstanceOf[HelperNewValue]
    Right(Some(List(newStatement.value)))
  }

}
