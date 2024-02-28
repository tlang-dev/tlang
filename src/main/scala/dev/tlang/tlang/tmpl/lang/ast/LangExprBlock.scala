package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent, TmplNode}

case class LangExprBlock(context: Option[ContextContent], var exprs: List[TmplNode[_]]) extends LangExprContent[LangExprBlock] with Context {
  //  override def deepCopy(): LangExprBlock = LangExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[TmplNode[_]]))

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangExprBlock.model),
    Some(List(
      //      BuildAstTmpl.createAttrList(context, "exprs", exprs.map(_.toEntity)),
    ))
  )

  //  override def toModel: ModelSetEntity = LangExprBlock.model

  override def getContext: Option[ContextContent] = context

  override def getElement: LangExprBlock = this

  override def getType: Type = LangExprBlock.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangExprBlock.model
}

object LangExprBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrArray(None, Some("exprs")),
  )))
}