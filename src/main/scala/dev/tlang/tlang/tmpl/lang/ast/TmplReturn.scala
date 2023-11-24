package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation

case class TmplReturn(context: Option[ContextContent], var operation: TmplOperation) extends TmplExpression[TmplReturn] with AstContext {
  override def deepCopy(): TmplReturn = TmplReturn(context, operation.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplReturn]): Int = 0

  override def getElement: TmplReturn = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplReturn.name)),
    Some(List(

    ))
  )
}
