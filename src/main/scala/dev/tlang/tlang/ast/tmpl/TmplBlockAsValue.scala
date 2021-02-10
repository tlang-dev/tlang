package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.interpreter.Value
import io.sorne.tlang.ast.common.value.TLangType
import io.sorne.tlang.interpreter.Value
import io.sorne.tlang.interpreter.context.Context

case class TmplBlockAsValue(block: TmplBlock, context: Context) extends Value[TmplBlock] {
  override def getValue: TmplBlock = this.block

  override def getType: String = TmplBlockAsValue.getType

  override def compareTo(value: Value[TmplBlock]): Int = 0
}

object TmplBlockAsValue extends TLangType {
  override def getType: String = "TmplBlock"
}
