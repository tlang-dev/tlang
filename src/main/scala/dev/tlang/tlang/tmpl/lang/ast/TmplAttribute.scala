package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation

case class TmplAttribute(context: Option[ContextContent], var attr: Option[TmplID], var `type`: Option[TmplType], var value: TmplOperation) extends TmplNode[TmplAttribute] {
  override def deepCopy(): TmplAttribute = TmplAttribute(context,
    if (attr.isDefined) Some(attr.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    value.deepCopy()
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplAttribute]): Int = 0

  override def getElement: TmplAttribute = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplAttribute.name)),
    Some(List())
  )
}
