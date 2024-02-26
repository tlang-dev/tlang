package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangMultiValue(context: Null, var values: List[LangValueType[_]]) extends LangValueType[LangMultiValue] with AstContext {
//  override def deepCopy(): LangMultiValue = LangMultiValue(context, values.map(_.deepCopy().asInstanceOf[LangValueType[_]]))

  override def getContext: Null = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangMultiValue.modelName)),
    Some(List(
      BuildLang.createArray(context, "values", values.map(_.toEntity))
    ))
  )

  override def getElement: LangMultiValue = this

  override def getType: Type = LangMultiValue.modelName
}

object LangMultiValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("values"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}