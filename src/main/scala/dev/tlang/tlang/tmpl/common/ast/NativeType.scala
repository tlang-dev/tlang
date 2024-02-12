package dev.tlang.tlang.tmpl.common.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.TmplNode
import tlang.core.Null
import tlang.internal.ContextContent

case class NativeType[T](context: Null[ContextContent], statement: T) extends TmplNode[T] {
  override def toEntity: EntityValue = EntityValue(context, None, None)

  override def toModel: ModelSetEntity = NativeType.model

  override def getElement: T = statement

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): NativeType[_] = NativeType(context, statement)

}

object NativeType {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, None, None, None)
}
