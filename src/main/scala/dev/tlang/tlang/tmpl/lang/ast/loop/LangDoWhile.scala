package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent}

case class LangDoWhile(context: Option[ContextContent], content: LangExprContent[_], cond: LangOperation) extends LangExpression[LangDoWhile] {
  //  override def deepCopy(): LangDoWhile =
  //    LangDoWhile(context, content.deepCopy().asInstanceOf[LangExprContent[_]], cond.deepCopy())

  override def getContext: Option[ContextContent] = context


  override def getElement: LangDoWhile = this

  override def getType: Type = LangDoWhile.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangDoWhile.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "content", content.toEntity),
      //      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangDoWhile.model
}

object LangDoWhile {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
//    BuildAstTmpl.createModelAttrEntity(None, Some("content"), LangExprContent.model.getType),
    BuildAstTmpl.createModelAttrEntity(None, Some("cond"), LangOperation.modelType),
  )))
}
