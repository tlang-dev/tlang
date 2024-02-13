package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent, TmplID}

case class LangTextValue(context: Null[ContextContent], var value: TmplID) extends LangPrimitiveValue[LangTextValue] with AstContext {
  override def deepCopy(): LangTextValue = LangTextValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Null[ContextContent] = context

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

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TmplID.name)),
  )))
}