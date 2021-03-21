package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.model.set.ModelSetRefValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}

case class EntityValue(context: Option[ContextContent],
                       `type`: Option[String],
                       params: Option[List[ComplexAttribute]] = None,
                       attrs: Option[List[ComplexAttribute]] = None,
                       scope: Scope = Scope())
  extends PrimitiveValue[EntityValue] with ModelSetRefValue with AstContext {

  override def getElement: EntityValue = this

  override def getType: String = if (`type`.isDefined) `type`.get else getClass.getName

  override def compareTo(value: Value[EntityValue]): Int = 0

  override def add(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def subtract(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def multiply(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def divide(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def modulo(value: EntityValue): Either[ExecError, EntityValue] = Left(NotImplemented())

  override def getContext: Option[ContextContent] = context
}
