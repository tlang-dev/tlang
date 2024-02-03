package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.primitive.LangPrimitiveValue

case class StyleArray(context: Option[ContextContent], values: List[StyleAttribute[_]]) extends LangPrimitiveValue[StyleArray] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleArray.model

  override def compareTo(value: Value[StyleArray]): Int = 0

  override def getElement: StyleArray = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleArray = StyleArray(context, values.map(_.deepCopy().asInstanceOf[StyleAttribute[_]]))

  override def getContext: Option[ContextContent] = context
}

object StyleArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}
