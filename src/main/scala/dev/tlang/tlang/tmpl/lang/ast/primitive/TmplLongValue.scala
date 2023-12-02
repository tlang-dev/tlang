package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplValueAst}

case class TmplLongValue(context: Option[ContextContent], value: Long) extends TmplPrimitiveValue[TmplLongValue] with AstContext {
  override def deepCopy(): TmplLongValue = TmplLongValue(context, value)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplLongValue]): Int = this.value.compareTo(value.getElement.value)

  override def getElement: TmplLongValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langLong.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
