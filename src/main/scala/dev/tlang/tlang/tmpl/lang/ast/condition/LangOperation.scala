package dev.tlang.tlang.tmpl.lang.ast.condition

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type, Value}
import tlang.internal.{ContextContent, DeepCopy, TmplNode}

case class LangOperation(context: Null[ContextContent], var content: Either[LangOperation, LangExpression[_]], var next: Option[(Operator.operator, LangOperation)] = None) extends DeepCopy with TmplNode[LangOperation] {
  override def deepCopy(): LangOperation = LangOperation(context,
    content match {
      case Left(value) => Left(value.deepCopy())
      case Right(value) => Right(value.deepCopy().asInstanceOf[LangExpression[_]])
    },
    if (next.isDefined) Some((next.get._1, next.get._2.deepCopy())) else None)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[LangOperation]): Int = 0

  override def getElement: LangOperation = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangOperation.modelType)),
    Some(List(
      BuildLang.createAttrEntity(context, "content", content match {
        case Left(value) => value.toEntity
        case Right(value) => value.toEntity
      }),
    ))
  )

  override def toModel: ModelSetEntity = LangOperation.model
}

object LangOperation {

  val modelType: Type = ManualType(LangModel.pkg, this.getClass.getSimpleName.replace("$", ""))

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
}