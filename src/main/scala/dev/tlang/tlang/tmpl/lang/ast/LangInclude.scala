package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangInclude(context: Option[ContextContent], calls: List[CallObject]) extends LangExpression[LangInclude] {
  override def deepCopy(): LangInclude = LangInclude(context, calls)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangInclude]): Int = 0

  override def getElement: LangInclude = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangInclude.name)),
    Some(List(
      BuildLang.createArray(context, "calls", calls.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = LangInclude.model
}

object LangInclude {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("calls"), ModelSetType(None, ArrayValue.getType)),
  )))
}
