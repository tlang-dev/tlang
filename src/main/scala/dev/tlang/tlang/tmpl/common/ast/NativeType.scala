package dev.tlang.tlang.tmpl.common.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class NativeType[T](context: Null[ContextContent], statement: T) extends TmplNode[T] {
  override def toEntity: EntityValue = EntityValue(context, None, None)

  override def toModel: ModelSetEntity = NativeType.model

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): NativeType[_] = NativeType(context, statement)

  override def getContext: Null[ContextContent] = context
}

object NativeType {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, None, None, None)
}
