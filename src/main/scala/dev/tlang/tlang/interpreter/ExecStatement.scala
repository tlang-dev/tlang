package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.condition.ConditionBlock
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.Context

object ExecStatement extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case stmt: HelperIf => ExecIf.run(stmt, context)
      case stmt: CallObject => ExecCallObject.run(stmt, context)
      case stmt: HelperFor => ExecFor.run(stmt, context)
      case stmt: HelperFunc => ExecFunc.run(stmt, context)
      case stmt: ConditionBlock => ExecConditionBlock.run(stmt, context)
      case stmt: HelperInternalFunc => ExecInternalFunc.run(stmt, context)
      case stmt: AssignVar => ExecAssignVar.run(stmt, context)
      case stmt: PrimitiveValue[_] => ExecPrimitiveValue.run(stmt, context)
      case stmt: MultiValue => ExecMultiValue.run(stmt, context)
      case stmt: SimpleValueType[_] => ExecSimpleValue.run(stmt, context)
      case stmt: ComplexValueType[_] => ExecComplexValue.run(stmt, context)
    }
  }

}
