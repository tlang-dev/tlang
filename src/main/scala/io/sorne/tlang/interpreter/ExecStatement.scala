package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.interpreter.context.Context

object ExecStatement extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case HelperIf(_, _, _) => ExecIf.run(statement, context)
      case HelperCallObject(_) => ExecCallObject.run(statement, context)
      case HelperFor() => ExecFor.run(statement, context)
      case HelperFunc(_, _, _, _) => ExecFunc.run(statement, context)
      case HelperConditionBlock(_, _, _) => ExecConditionBlock.run(statement, context)
      case HelperInternalFunc(_) => ExecInternalFunc.run(statement, context)
    }
  }
}
