package dev.tlang.tlang.tmpl.common.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode

case class NativeType[T](context: Option[ContextContent], statement: T) extends TmplNode[T] {
  override def toEntity: EntityValue = EntityValue(context, None, None)

  override def toModel: ModelSetEntity = NativeType.model

  override def compareTo(value: Value[T]): Int = 0

  override def getElement: T = statement

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): NativeType[_] = NativeType(context, statement)

  override def getContext: Option[ContextContent] = context
}

object NativeType {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, None, None, None)
}
