package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.model.set.ModelSetRefValue
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}

case class EntityValue(`type`: Option[String], params: Option[List[ComplexAttribute]] = None, attrs: Option[List[ComplexAttribute]] = None)
  extends PrimitiveValue[EntityValue] with ModelSetRefValue {

  override def getValue: EntityValue = this

  override def getType: String = if (`type`.isDefined) `type`.get else getClass.getName

  override def compareTo(value: Value[EntityValue]): Int = 0

  override def add(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def subtract(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def multiply(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def divide(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def modulo(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())
}
