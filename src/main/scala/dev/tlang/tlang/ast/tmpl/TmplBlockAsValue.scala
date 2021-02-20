package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Context

case class TmplBlockAsValue(block: TmplBlock, context: Context) extends Value[TmplBlock] with DeepCopy {
  override def getValue: TmplBlock = this.block

  override def getType: String = TmplBlockAsValue.getType

  override def compareTo(value: Value[TmplBlock]): Int = 0

  override def deepCopy(): TmplBlockAsValue = new TmplBlockAsValue(block.deepCopy(), context)
}

object TmplBlockAsValue extends TLangType {
  override def getType: String = "TmplBlock"
}
