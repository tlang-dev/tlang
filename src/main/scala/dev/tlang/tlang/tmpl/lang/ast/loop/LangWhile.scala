package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangExprContent, LangExpression}

case class LangWhile(context: Option[ContextContent], cond: LangOperation, content: LangExprContent[_]) extends LangExpression[LangWhile] with AstContext {
  override def deepCopy(): LangWhile =
    LangWhile(context, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangWhile]): Int = 0

  override def getElement: LangWhile = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangWhile.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangWhile.model
}

object LangWhile {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
