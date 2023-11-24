package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplParam(context: Option[ContextContent], var annots: Option[List[TmplAnnotation]] = None, var name: TmplID, var `type`: Option[TmplType]) extends TmplNode[TmplParam] {
  override def deepCopy(): TmplParam = TmplParam(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplParam]): Int = 0

  override def getElement: TmplParam = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplParam.name)),
    Some(List())
  )
}
