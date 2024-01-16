package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangExprContent, LangExpression, LangID}

case class LangFor(context: Option[ContextContent], var variable: LangID, var start: Option[LangOperation], forType: ForType.ForType, var cond: LangOperation, var content: LangExprContent[_]) extends LangExpression[LangFor] {
  override def deepCopy(): LangFor = LangFor(context,
    variable.deepCopy().asInstanceOf[LangID],
    if (start.isDefined) Some(start.get.deepCopy()) else None,
    forType, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]])

  override def compareTo(value: Value[LangFor]): Int = 0

  override def getElement: LangFor = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangFor.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangFor.model
}

object LangFor {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

object ForType extends Enumeration {
  type ForType = Value
  val IN, TO, UNTIL = Value
}