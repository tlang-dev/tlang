package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.Null
import tlang.internal.ContextContent

case class LangInclude(context: Null[ContextContent], calls: List[CallObject]) extends LangExpression[LangInclude] {
  override def deepCopy(): LangInclude = LangInclude(context, calls)

  override def getContext: Null[ContextContent] = context

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

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("calls"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
