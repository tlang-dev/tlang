package dev.tlang.tlang.ast.common.operation

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.set.ModelSetRefValue
import dev.tlang.tlang.tmpl.AstValue
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class Operation(context: Option[ContextContent], var expectedType: Option[Type],
                     content: Either[Operation, ComplexValueStatement[_]],
                     next: Option[(Operator.operator, Operation)] = None)
  extends HelperStatement with AstValue with ModelSetRefValue {

  override def getType: Type = Operation.getType

  override def getElement: Operation = this

  override def getContext: Option[ContextContent] = context
}

object Operation extends TLangType {
  override def getType: Type = ClassType.of(this.getClass)

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
