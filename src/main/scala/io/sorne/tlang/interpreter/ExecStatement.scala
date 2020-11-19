package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.helper.call.HelperCallObject
import io.sorne.tlang.interpreter.context.Context

object ExecStatement extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case stmt: HelperIf => ExecIf.run(stmt, context)
      case stmt: HelperCallObject => ExecCallObject.run(stmt, context)
      case stmt: HelperFor => ExecFor.run(stmt, context)
      case stmt: HelperFunc => ExecFunc.run(stmt, context)
      case stmt: HelperConditionBlock => ExecConditionBlock.run(stmt, context)
      case stmt: HelperInternalFunc => ExecInternalFunc.run(stmt, context)
      case stmt: HelperAssignVar => ExecAssignVar.run(stmt, context)
      case stmt: HelperNewValue => ExecNewValue.run(stmt, context)
      case stmt: HelperNewMultiValue => ExecNewMultiValue.run(stmt, context)
    }
  }

}
