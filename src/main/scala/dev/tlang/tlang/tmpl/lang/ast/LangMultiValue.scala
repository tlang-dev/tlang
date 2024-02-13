package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent}

case class LangMultiValue(context: Null[ContextContent], var values: List[LangValueType[_]]) extends LangValueType[LangMultiValue] with AstContext {
  override def deepCopy(): LangMultiValue = LangMultiValue(context, values.map(_.deepCopy().asInstanceOf[LangValueType[_]]))

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[LangMultiValue]): Int = 0

  override def getElement: LangMultiValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangMultiValue.name)),
    Some(List(
      BuildLang.createArray(context, "values", values.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = LangMultiValue.model
}

object LangMultiValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("values"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}