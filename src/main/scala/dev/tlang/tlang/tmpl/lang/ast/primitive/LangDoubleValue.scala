package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, TLangDouble}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent}

case class LangDoubleValue(context: Null[ContextContent], value: Double) extends LangPrimitiveValue[LangDoubleValue] with AstContext {
  override def deepCopy(): LangDoubleValue = LangDoubleValue(context, value)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[LangDoubleValue]): Int = 0

  override def getElement: LangDoubleValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangDoubleValue.name)),
    Some(List(
      BuildLang.createAttrDouble(context, "value", value)
    ))
  )

  override def toModel: ModelSetEntity = LangDoubleValue.model
}

object LangDoubleValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TLangDouble.getType)),
  )))
}
