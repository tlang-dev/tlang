package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangID, LangModel}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangTextValue(context: Option[ContextContent], var value: LangID) extends LangPrimitiveValue[LangTextValue] with AstContext {
  override def deepCopy(): LangTextValue = LangTextValue(context, value.deepCopy().asInstanceOf[LangID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangTextValue]): Int = this.value.toString.compareTo(value.toString)

  override def getElement: LangTextValue = this

  override def getType: String = getClass.getSimpleName

  override def toString: String = value.toString

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangTextValue.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangTextValue.model
}

object LangTextValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("value"), ModelSetType(None, LangID.name)),
  )))
}