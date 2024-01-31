package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangIf(context: Option[ContextContent], cond: LangOperation, content: LangExprContent[_], elseBlock: Option[Either[LangExprContent[_], LangIf]]) extends LangExpression[LangIf] with AstContext {
  override def deepCopy(): LangIf = LangIf(context, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]],
    if (elseBlock.isDefined) elseBlock.get match {
      case Left(value) => Some(Left(value.deepCopy().asInstanceOf[LangExprContent[_]]))
      case Right(value) => Some(Right(value.deepCopy()))
    } else None,
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangIf]): Int = 0

  override def getElement: LangIf = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangIf.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
      BuildLang.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangIf.model
}

object LangIf {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("cond"), ModelSetType(None, LangOperation.name)),
    ModelSetAttribute(None, Some("content"), ModelSetType(None, LangExprContent.name)),
    ModelSetAttribute(None, Some("elseBlock"), ModelSetType(None, NullValue.name)),
  )))
}
