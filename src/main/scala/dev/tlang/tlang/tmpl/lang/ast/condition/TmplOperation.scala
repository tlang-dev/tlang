package dev.tlang.tlang.tmpl.lang.ast.condition

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{DeepCopy, TmplExprAst, TmplExpression, TmplLangAst, TmplNode}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class TmplOperation(context: Option[ContextContent], var content: Either[TmplOperation, TmplExpression[_]], var next: Option[(Operator.operator, TmplOperation)] = None) extends DeepCopy with TmplNode[TmplOperation] {
  override def deepCopy(): TmplOperation = TmplOperation(context,
    content match {
      case Left(value) => Left(value.deepCopy())
      case Right(value) => Right(value.deepCopy().asInstanceOf[TmplExpression[_]])
    },
    if (next.isDefined) Some((next.get._1, next.get._2.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplOperation]): Int = 0

  override def getElement: TmplOperation = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplExprAst.langOperation.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "content", content match {
        case Left(value) => value.toEntity
        case Right(value) => value.toEntity
      }),
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
