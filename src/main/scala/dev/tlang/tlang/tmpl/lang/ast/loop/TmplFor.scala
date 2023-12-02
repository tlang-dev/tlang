package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplExprContent, TmplExpression, TmplID, TmplLangAst, TmplLoopAst}

case class TmplFor(context: Option[ContextContent], var variable: TmplID, var start: Option[TmplOperation], forType: ForType.ForType, var cond: TmplOperation, var content: TmplExprContent[_]) extends TmplExpression[TmplFor] {
  override def deepCopy(): TmplFor = TmplFor(context,
    variable.deepCopy().asInstanceOf[TmplID],
    if (start.isDefined) Some(start.get.deepCopy()) else None,
    forType, cond.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent[_]])

  override def compareTo(value: Value[TmplFor]): Int = 0

  override def getElement: TmplFor = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLoopAst.tmplFor.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}

object ForType extends Enumeration {
  type ForType = Value
  val IN, TO, UNTIL = Value
}