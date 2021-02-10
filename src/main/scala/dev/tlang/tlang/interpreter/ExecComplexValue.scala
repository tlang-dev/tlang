package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.condition.ConditionBlock
import dev.tlang.tlang.ast.common.value.{MultiValue, PrimitiveValue}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.condition.ConditionBlock
import dev.tlang.tlang.ast.common.value.{MultiValue, PrimitiveValue}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context

object ExecComplexValue extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val valueStatement = statement.asInstanceOf[ComplexValueStatement[_]]
    valueStatement match {
      case obj: CallObject => ExecCallObject.run(obj, context)
      case condition: ConditionBlock => ExecConditionBlock.run(condition, context)
      case values: MultiValue => ExecMultiValue.run(values, context)
      case value: PrimitiveValue[_] => ExecPrimitiveValue.run(value, context)
      case value: Value[_] => Right(Some(List(value)))
    }
  }
}
