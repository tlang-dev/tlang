package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplValueAst}

case class TmplBoolValue(context: Option[ContextContent], value: Boolean) extends TmplPrimitiveValue[TmplBoolValue] with AstContext {
  override def deepCopy(): TmplBoolValue = TmplBoolValue(context, if (value) true else false)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplBoolValue]): Int = 0

  override def getElement: TmplBoolValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langBool.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
