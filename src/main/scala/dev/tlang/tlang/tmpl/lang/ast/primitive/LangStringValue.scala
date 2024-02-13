package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangStringValue(context: Null[ContextContent], var value: TmplID) extends LangPrimitiveValue[LangStringValue] {
  override def deepCopy(): LangStringValue = LangStringValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def compareTo(value: Value[LangStringValue]): Int = this.value.toString.compareTo(value.toString)

  override def getElement: LangStringValue = this

  override def getType: String = getClass.getSimpleName

  override def toString: String = value.toString

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangStringValue.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangStringValue.model
}

object LangStringValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TmplID.name)),
  )))
}
