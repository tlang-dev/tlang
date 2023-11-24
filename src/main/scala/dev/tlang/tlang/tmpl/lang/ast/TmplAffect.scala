package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.call.TmplCallObj
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation

case class TmplAffect(context: Option[ContextContent], var variable: TmplCallObj, var value: TmplOperation) extends TmplExpression[TmplAffect] with AstContext {
  override def deepCopy(): TmplAffect = TmplAffect(context, variable.deepCopy(), value.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplAffect]): Int = 0

  override def getElement: TmplAffect = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langAffect.name)),
    Some(List())
  )
}
