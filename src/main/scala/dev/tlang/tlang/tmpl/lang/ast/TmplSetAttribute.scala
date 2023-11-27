package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation

case class TmplSetAttribute(context: Option[ContextContent], var name: Option[TmplID], var value: TmplOperation) extends TmplNode[TmplSetAttribute] {
  override def deepCopy(): TmplSetAttribute = TmplSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplSetAttribute]): Int = 0

  override def getElement: TmplSetAttribute = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplSetAttribute.name)),
    Some(List(

    )))

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
