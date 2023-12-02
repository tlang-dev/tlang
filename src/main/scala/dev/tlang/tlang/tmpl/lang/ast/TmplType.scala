package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.call.TmplCallFuncParam

case class TmplType(context: Option[ContextContent], var name: TmplID, var generic: Option[TmplGeneric] = None, isArray: Boolean = false, var currying: Option[List[TmplCallFuncParam]] = None) extends TmplNode[TmplType] {
  override def deepCopy(): TmplType = TmplType(context, name.deepCopy().asInstanceOf[TmplID],
    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
    if (isArray) true else false,
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None,
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplType]): Int = 0

  override def getElement: TmplType = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = ???

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
