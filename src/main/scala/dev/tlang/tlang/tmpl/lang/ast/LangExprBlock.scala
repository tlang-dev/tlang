package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangExprBlock(context: Option[ContextContent], var exprs: List[LangNode[_]]) extends LangExprContent[LangExprBlock] with AstContext {
  override def deepCopy(): LangExprBlock = LangExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[LangNode[_]]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangExprBlock]): Int = 0

  override def getElement: LangExprBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangExprBlock.name)),
    Some(List(
      BuildLang.createArray(context, "exprs", exprs.map(_.toEntity)),
    ))
  )

  override def toModel: ModelSetEntity = LangExprBlock.model
}

object LangExprBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}