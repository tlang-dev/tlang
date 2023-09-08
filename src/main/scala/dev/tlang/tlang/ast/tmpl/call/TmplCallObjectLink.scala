package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplNode}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplCallObjectLink(context: Option[ContextContent], var link: String = ".", var call: TmplCallObjType[_]) extends DeepCopy with TmplNode[TmplCallObjectLink] {
  override def deepCopy(): TmplCallObjectLink = TmplCallObjectLink(context, link, call.deepCopy().asInstanceOf[TmplCallObjType[_]])

  override def compareTo(value: Value[TmplCallObjectLink]): Int = 0

  override def getElement: TmplCallObjectLink = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context
}
