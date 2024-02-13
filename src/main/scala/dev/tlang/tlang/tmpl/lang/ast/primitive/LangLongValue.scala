package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, TLangLong}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent}

case class LangLongValue(context: Null[ContextContent], value: Long) extends LangPrimitiveValue[LangLongValue] with AstContext {
  override def deepCopy(): LangLongValue = LangLongValue(context, value)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[LangLongValue]): Int = this.value.compareTo(value.getElement.value)

  override def getElement: LangLongValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangLongValue.name)),
    Some(List(
      BuildLang.createAttrLong(context, "value", value)
    ))
  )

  override def toModel: ModelSetEntity = LangLongValue.model
}

object LangLongValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TLangLong.getType)),
  )))
}