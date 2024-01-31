package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.func.LangFuncParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangSpecialBlock(context: Option[ContextContent], var `type`: String, var curries: Option[List[LangFuncParam]], var content: Option[LangExprContent[_]]) extends LangExpression[LangSpecialBlock] with LangContent[LangSpecialBlock] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangSpecialBlock]): Int = 0

  override def deepCopy(): LangSpecialBlock = LangSpecialBlock(
    context,
    `type` = `type`,
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    content = if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[LangExprContent[_]]) else None)

  override def getElement: LangSpecialBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangSpecialBlock.name)),
    Some(List(
      BuildLang.createAttrStr(context, "tType", `type`),
      BuildLang.createAttrNull(context, "curries",
        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrNull(context, "content",
        if (content.isDefined) Some(content.get.toEntity) else None,
        None
      ),
    )))

  override def toModel: ModelSetEntity = LangSpecialBlock.model
}

object LangSpecialBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("tType"), ModelSetType(None, TLangString.getType)),
    ModelSetAttribute(None, Some("curries"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("content"), ModelSetType(None, NullValue.name)),
  )))
}
