package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class DataInclude(context: Null) extends TmplNode[DataInclude] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DataInclude.modelName)),
    Some(List(
    ))
  )

//  override def toModel: ModelSetEntity = DataInclude.model

  override def getType: Type = DataInclude.modelName

//  override def deepCopy(): Any = DataInclude(context)

  override def getContext: Null = context

  override def getElement: DataInclude = this
}

object DataInclude {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}