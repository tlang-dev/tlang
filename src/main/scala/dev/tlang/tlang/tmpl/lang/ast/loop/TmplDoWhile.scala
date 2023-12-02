package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplExprContent, TmplExpression, TmplLangAst, TmplLoopAst}

case class TmplDoWhile(context: Option[ContextContent], content: TmplExprContent[_], cond: TmplOperation) extends TmplExpression[TmplDoWhile] with AstContext {
  override def deepCopy(): TmplDoWhile =
    TmplDoWhile(context, content.deepCopy().asInstanceOf[TmplExprContent[_]], cond.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplDoWhile]): Int = 0

  override def getElement: TmplDoWhile = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLoopAst.tmplDoWhile.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
