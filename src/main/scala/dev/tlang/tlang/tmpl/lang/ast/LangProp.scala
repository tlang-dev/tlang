package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.{DeepCopy, TmplNode}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangProp(context: Option[ContextContent], var props: List[TmplID]) extends DeepCopy with AstContext with TmplNode[LangProp] {
  override def deepCopy(): LangProp = LangProp(context, props.map(_.deepCopy().asInstanceOf[TmplID]))

  override def getContext: Option[ContextContent] = context

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

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("props"), ModelSetType(None, ArrayValue.getType)),
  )))
}
