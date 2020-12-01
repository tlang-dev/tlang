package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.common.value.PrimitiveValue
import io.sorne.tlang.ast.helper.HelperStatement
import io.sorne.tlang.interpreter.context.Context

object ExecPrimitiveValue extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val newStatement = statement.asInstanceOf[PrimitiveValue[_]]
    Right(Some(List(newStatement)))
  }

}
