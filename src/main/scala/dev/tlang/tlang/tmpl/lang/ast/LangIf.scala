package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.{Entity, Type}
import tlang.internal.ContextContent

case class LangIf(context: Option[ContextContent], cond: LangOperation, content: LangExprContent[_], elseBlock: Option[Either[LangExprContent[_], LangIf]]) extends LangExpression[LangIf] {
  //  override def deepCopy(): LangIf = LangIf(context, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]],
  //    if (elseBlock.isDefined) elseBlock.get match {
  //      case Left(value) => Some(Left(value.deepCopy().asInstanceOf[LangExprContent[_]]))
  //      case Right(value) => Some(Right(value.deepCopy()))
  //    } else None,
  //  )

  override def getContext: Option[ContextContent] = context

  override def getElement: LangIf = this

  override def getType: Type = LangIf.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangIf.model),
    Some(List(
      //      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
      BuildAstTmpl.createAttrEntity(context, "content", Some(content.getType), content.toEntity),
    ))
  )

  //  override def toModel: ModelSetEntity = LangIf.model

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangIf.model
}

object LangIf {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("cond")),
    BuildAstTmpl.createModelAttrEntity(None, Some("content"), Entity.TYPE),
    BuildAstTmpl.createModelAttrNull(None, Some("elseBlock")),
  )))
}
