package dev.tlang.tlang.tmpl.lang.ast.condition

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangOperation(context: Option[ContextContent], var content: Either[LangOperation, LangExpression[_]], var next: Option[(Operator.operator, LangOperation)] = None) extends AstTmplNode {
  //  override def deepCopy(): LangOperation = LangOperation(context,
  //    content match {
  //      case Left(value) => Left(value.deepCopy())
  //      case Right(value) => Right(value.deepCopy().asInstanceOf[LangExpression[_]])
  //    },
  //    if (next.isDefined) Some((next.get._1, next.get._2.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context


  override def getElement: LangOperation = this

  override def getType: Type = LangOperation.modelType

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangOperation.model),
    Some(List(
      //      BuildLang.createAttrEntity(context, "content", content match {
      //        case Left(value) => value.toEntity
      //        case Right(value) => value.toEntity
      //      }),
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangOperation.model
}

object LangOperation {

  val modelType: Type = ManualType(LangModel.pkg, this.getClass.getSimpleName.replace("$", ""))

  val model: AstModel = AstModel(None, modelType, Some(LangModel.langNode), None, Some(List(
  )))
}