package io.sorne.tlang.ast.common.value

import io.sorne.tlang.ast.model.set.ModelSetRefValue
import io.sorne.tlang.interpreter.Value

case class EntityValue(`type`: Option[String], params: Option[List[ComplexAttribute]] = None, attrs: Option[List[ComplexAttribute]] = None)
  extends PrimitiveValue[EntityValue] with ModelSetRefValue {

  override def getValue: EntityValue = this

  override def getType: String = if (`type`.isDefined) `type`.get else getClass.getName

  override def compareTo(value: Value[EntityValue]): Int = 0
}
