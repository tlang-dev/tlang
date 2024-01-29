package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangDoWhile(context: Option[ContextContent], content: LangExprContent[_], cond: LangOperation) extends LangExpression[LangDoWhile] with AstContext {
  override def deepCopy(): LangDoWhile =
    LangDoWhile(context, content.deepCopy().asInstanceOf[LangExprContent[_]], cond.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangDoWhile]): Int = 0

  override def getElement: LangDoWhile = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangDoWhile.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "content", content.toEntity),
      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangDoWhile.model
}

object LangDoWhile {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("content"), ModelSetType(None, LangExprContent.name)),
    ModelSetAttribute(None, Some("cond"), ModelSetType(None, LangOperation.name)),
  )))
}
