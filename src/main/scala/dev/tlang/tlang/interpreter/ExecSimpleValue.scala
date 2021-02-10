package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallObject, SimpleValueStatement}
import dev.tlang.tlang.ast.common.value.PrimitiveValue
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context

object ExecSimpleValue extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val valueStatement = statement.asInstanceOf[SimpleValueStatement[_]]
    valueStatement match {
      case obj: CallObject => ExecCallObject.run(obj, context)
      case value: PrimitiveValue[_] => ExecPrimitiveValue.run(value, context)
    }
  }
}
