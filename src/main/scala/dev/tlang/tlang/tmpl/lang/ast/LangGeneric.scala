package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.Null
import tlang.internal.{ContextContent, TmplNode}

case class LangGeneric(context: Null[ContextContent], var types: List[LangType]) extends TmplNode[LangGeneric] {
  override def deepCopy(): LangGeneric = LangGeneric(context, types.map(_.deepCopy()))

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangGeneric.name)),
    Some(List(
      BuildLang.createArray(context, "types", types.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = LangGeneric.model
}

object LangGeneric {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("types"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
