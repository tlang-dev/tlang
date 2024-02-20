package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.func.LangFuncParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangSpecialBlock(context: Null[ContextContent], var `type`: String, var curries: Option[List[LangFuncParam]], var content: Option[LangExprContent[_]]) extends LangExpression[LangSpecialBlock] with LangContent[LangSpecialBlock] with AstContext {
  override def getContext: Null[ContextContent] = context

  override def deepCopy(): LangSpecialBlock = LangSpecialBlock(
    context,
    `type` = `type`,
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    content = if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[LangExprContent[_]]) else None)

  override def getElement: LangSpecialBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangSpecialBlock.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "tType", `type`),
      BuildLang.createAttrNull(context, "curries",
        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrNull(context, "content",
        content,
        None
      ),
    )))

  override def toModel: ModelSetEntity = LangSpecialBlock.model
}

object LangSpecialBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("curries"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
