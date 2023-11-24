package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplExprBlock(context: Option[ContextContent], var exprs: List[TmplNode[_]]) extends TmplExprContent[TmplExprBlock] with AstContext {
  override def deepCopy(): TmplExprBlock = TmplExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[TmplNode[_]]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplExprBlock]): Int = 0

  override def getElement: TmplExprBlock = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplImplAst.langImpl.name)),
    Some(List())
  )
}
