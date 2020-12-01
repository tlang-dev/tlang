package io.sorne.tlang.ast.tmpl

import io.sorne.tlang.ast.common.value.TLangType
import io.sorne.tlang.interpreter.Value

case class TmplBlockAsValue(block: TmplBlock, params: List[Value[_]]) extends Value[TmplBlock] {
  override def getValue: TmplBlock = this.block

  override def getType: String = TmplBlockAsValue.getType

  override def compareTo(value: Value[TmplBlock]): Int = 0
}

object TmplBlockAsValue extends TLangType {
  override def getType: String = "TmplBlock"
}
