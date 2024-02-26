package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.{AssignVar, MultiValue}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context
import tlang.core.{Null, Value}

object ExecAssignVar extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value]]] = {
    val varStatement = statement.asInstanceOf[AssignVar]

    ExecOperation.run(varStatement.value, context) match {
      case Left(error) =>
        Left(error)
      case Right(value) => value match {
        case None => Left(NoValue("Value to assign was empty", varStatement.context))
        case Some(value) =>
          if (value.isEmpty) Left(NoValue("Value to assign was empty", varStatement.context))
          else if (value.size == 1) {
            context.scopes.last.variables.addOne(varStatement.name -> value.head)
            Right(Some(List(value.head)))
          } else {
            val values = MultiValue(Null.empty(), value)
            context.scopes.last.variables.addOne(varStatement.name -> values.asInstanceOf[Value])
            Right(Some(value))
          }
      }
    }
  }

}
