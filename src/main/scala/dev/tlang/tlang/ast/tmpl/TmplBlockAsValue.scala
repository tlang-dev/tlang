package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Context

case class TmplBlockAsValue(astContext: Option[ContextContent], block: TmplBlock, context: Context) extends Value[TmplBlock] with DeepCopy {
  override def getValue: TmplBlock = this.block

  override def getType: String = TmplBlockAsValue.getType

  override def compareTo(value: Value[TmplBlock]): Int = 0

  override def deepCopy(): TmplBlockAsValue = new TmplBlockAsValue(astContext, block.deepCopy(), context)

  override def getContext: Option[ContextContent] = astContext
}

object TmplBlockAsValue extends TLangType {
  override def getType: String = "TmplBlock"
}
