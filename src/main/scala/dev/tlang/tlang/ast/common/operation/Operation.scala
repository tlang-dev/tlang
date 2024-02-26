package dev.tlang.tlang.ast.common.operation

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.set.ModelSetRefValue
import tlang.core.{Null, Type, Value}
import tlang.internal.ClassType

case class Operation(context: Null, expectedType: Option[ValueType],
                     content: Either[Operation, ComplexValueStatement[_]],
                     next: Option[(Operator.operator, Operation)] = None)
  extends HelperStatement with Value with ModelSetRefValue {

  override def getValue: Value = this

  override def getType: Type = Operation.getType

  //  override def compareTo(value: Value[Operation]): Int = 0

}

object Operation extends TLangType {
  override def getType: Type = ClassType.of(this.getClass)

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
