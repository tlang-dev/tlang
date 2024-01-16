package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class LangProp(context: Option[ContextContent], var props: List[LangID]) extends DeepCopy with AstContext with LangNode[LangProp] {
  override def deepCopy(): LangProp = LangProp(context, props.map(_.deepCopy().asInstanceOf[LangID]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangProp]): Int = 0

  override def getElement: LangProp = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplProp.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
