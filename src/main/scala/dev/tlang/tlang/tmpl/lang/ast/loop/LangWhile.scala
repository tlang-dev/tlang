package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent}

case class LangWhile(context: Option[ContextContent], cond: LangOperation, content: LangExprContent[_]) extends LangExpression[LangWhile] {
  //  override def deepCopy(): LangWhile =
  //    LangWhile(context, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]])

  override def getElement: LangWhile = this

  override def getType: Type = LangWhile.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangWhile.model),
    Some(List(
      //      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
      //      BuildAstTmpl.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangWhile.model
}

object LangWhile {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("cond"), LangOperation.modelType),
//    BuildAstTmpl.createModelAttrEntity(None, Some("content"), LangExprContent.model),
  )))
}
