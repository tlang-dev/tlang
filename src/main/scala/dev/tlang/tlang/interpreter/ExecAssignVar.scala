package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.{AssignVar, MultiValue}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context

object ExecAssignVar extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val varStatement = statement.asInstanceOf[AssignVar]

    ExecOperation.run(varStatement.value, context) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case None => Left(NoValue("Value to assign was empty"))
        case Some(value) =>
          if (value.isEmpty) Left(NoValue("Value to assign was empty"))
          else if (value.size == 1) {
            context.scopes.last.variables.addOne(varStatement.name -> value.head)
            Right(Some(List(value.head)))
          } else {
            val values = MultiValue(None, value)
            context.scopes.last.variables.addOne(varStatement.name -> values)
            Right(Some(value))
          }
      }
    }
  }

}
