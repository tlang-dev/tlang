package dev.tlang.tlang.ast.common.operation

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.set.ModelSetRefValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class Operation(context: Option[ContextContent], expectedType: Option[ValueType],
                     content: Either[Operation, ComplexValueStatement[_]],
                     next: Option[(Operator.operator, Operation)] = None)
  extends HelperStatement with Value[Operation] with ModelSetRefValue {

  override def getContext: Option[ContextContent] = context

  override def getElement: Operation = this

  override def getType: String = Operation.getType

  override def compareTo(value: Value[Operation]): Int = 0
}

object Operation extends TLangType {
  override def getType: String = getClass.getSimpleName

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
