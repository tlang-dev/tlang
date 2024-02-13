package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import dev.tlang.tlang.tmpl.{DeepCopy, TmplNode}
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent, TmplID}

case class LangProp(context: Null[ContextContent], var props: List[TmplID]) extends DeepCopy with AstContext with TmplNode[LangProp] {
  override def deepCopy(): LangProp = LangProp(context, props.map(_.deepCopy().asInstanceOf[TmplID]))

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[LangProp]): Int = 0

  override def getElement: LangProp = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangProp.name)),
    Some(List(
      BuildLang.createArray(context, "props", props.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = LangProp.model
}

object LangProp {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("props"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
