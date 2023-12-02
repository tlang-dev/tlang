package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class TmplReturn(context: Option[ContextContent], var operation: TmplOperation) extends TmplExpression[TmplReturn] with AstContext {
  override def deepCopy(): TmplReturn = TmplReturn(context, operation.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplReturn]): Int = 0

  override def getElement: TmplReturn = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.langReturn.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "operation", operation.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
