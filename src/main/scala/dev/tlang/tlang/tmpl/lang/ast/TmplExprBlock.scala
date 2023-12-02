package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class TmplExprBlock(context: Option[ContextContent], var exprs: List[TmplNode[_]]) extends TmplExprContent[TmplExprBlock] with AstContext {
  override def deepCopy(): TmplExprBlock = TmplExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[TmplNode[_]]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplExprBlock]): Int = 0

  override def getElement: TmplExprBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.langExprBlock.name)),
    Some(List(
      BuildLang.createArray(context, "exprs", exprs.map(_.toEntity)),
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
