package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.func.LangFuncParam
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent}

case class LangSpecialBlock(context: Option[ContextContent], var `type`: String, var curries: Option[List[LangFuncParam]], var content: Option[LangExprContent[_]]) extends LangExpression[LangSpecialBlock] with LangContent[LangSpecialBlock] with Context {
  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): LangSpecialBlock = LangSpecialBlock(
  //    context,
  //    `type` = `type`,
  //    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
  //    content = if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[LangExprContent[_]]) else None)

  override def getElement: LangSpecialBlock = this

  override def getType: Type = LangSpecialBlock.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangSpecialBlock.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "tType", `type`),
      //      BuildLang.createAttrNull(context, "curries",
      //        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "content",
      //        content,
      //        None
      //      ),
    )))

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangSpecialBlock.model
}

object LangSpecialBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrStr(None, Some("tType")),
    BuildAstTmpl.createModelAttrNull(None, Some("curries")),
    BuildAstTmpl.createModelAttrNull(None, Some("content")),
  )))
}
