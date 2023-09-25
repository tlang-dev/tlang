package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.call.TmplCallFuncParam
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplType(context: Option[ContextContent], var name: TmplID, var generic: Option[TmplGeneric] = None, isArray: Boolean = false, var currying: Option[List[TmplCallFuncParam]] = None) extends TmplNode[TmplType] {
  override def deepCopy(): TmplType = TmplType(context, name.deepCopy().asInstanceOf[TmplID],
    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
    if (isArray) true else false,
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None,
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplType]): Int = 0

  override def getElement: TmplType = this

  override def getType: String = getClass.getName
}
