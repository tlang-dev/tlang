package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.PrimitiveValue
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context

object ExecPrimitiveValue extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val newStatement = statement.asInstanceOf[PrimitiveValue[_]]
    Right(Some(List(newStatement)))
  }

}
