package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.value.{EntityImpl, MultiValue, PrimitiveValue}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Value

object ExecComplexValue extends Executor {
  override def run(statement: HelperStatement, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    val valueStatement = statement.asInstanceOf[ComplexValueStatement[_]]
    valueStatement match {
      case obj: CallObject => ExecCallObject.run(obj, context)
      case values: MultiValue => ExecMultiValue.run(values, context)
      case value: PrimitiveValue[_] => ExecPrimitiveValue.run(value, context)
      case value: Value => Right(Some(List(value)))
    }
  }
}
