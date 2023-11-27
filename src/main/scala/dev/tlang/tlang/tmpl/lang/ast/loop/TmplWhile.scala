package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplExprContent, TmplExpression, TmplLangAst, TmplLoopAst}

case class TmplWhile(context: Option[ContextContent], cond: TmplOperation, content: TmplExprContent[_]) extends TmplExpression[TmplWhile] with AstContext {
  override def deepCopy(): TmplWhile =
    TmplWhile(context, cond.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent[_]])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplWhile]): Int = 0

  override def getElement: TmplWhile = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLoopAst.tmplWhile.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
