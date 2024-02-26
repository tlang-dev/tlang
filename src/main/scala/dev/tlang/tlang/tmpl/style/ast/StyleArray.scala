package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast.primitive.LangPrimitiveValue
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class StyleArray(context: Null, values: List[StyleAttribute[_]]) extends LangPrimitiveValue[StyleArray] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, StyleArray.modelName)),
    Some(List())
  )

  override def getElement: StyleArray = this

  override def getType: Type = StyleArray.modelName

//  override def deepCopy(): StyleArray = StyleArray(context, values.map(_.deepCopy().asInstanceOf[StyleAttribute[_]]))

  override def getContext: Null = context
}

object StyleArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, StyleModel.styleModel.name)), None, Some(List(
  )))
}
