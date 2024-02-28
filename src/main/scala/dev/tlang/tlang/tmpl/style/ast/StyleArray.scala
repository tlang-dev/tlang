package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.primitive.LangPrimitiveValue
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.ContextContent

case class StyleArray(context: Option[ContextContent], values: List[StyleAttribute[_]]) extends LangPrimitiveValue[StyleArray] {
  override def toEntity: AstEntity = AstEntity(context,
    Some(StyleArray.model),
    Some(List())
  )

  override def getElement: StyleArray = this

  override def getType: Type = StyleArray.modelName

  //  override def deepCopy(): StyleArray = StyleArray(context, values.map(_.deepCopy().asInstanceOf[StyleAttribute[_]]))

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = StyleArray.model
}

object StyleArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(StyleModel.styleModel), None, Some(List(
  )))
}
