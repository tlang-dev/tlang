package dev.tlang.tlang.interpreter

import io.sorne.tlang.ast.common.call.{CallObject, SimpleValueStatement}
import io.sorne.tlang.ast.common.value.PrimitiveValue
import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.interpreter.context.Context

object ExecSimpleValue extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val valueStatement = statement.asInstanceOf[SimpleValueStatement[_]]
    valueStatement match {
      case obj: CallObject => ExecCallObject.run(obj, context)
      case value: PrimitiveValue[_] => ExecPrimitiveValue.run(value, context)
    }
  }
}
