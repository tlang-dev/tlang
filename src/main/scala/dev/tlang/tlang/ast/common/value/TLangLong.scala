package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

class TLangLong(context: Option[ContextContent], value: Long) extends PrimitiveValue[Long] with AstContext {
  override def getValue: Long = value

  override def getType: String = TLangLong.getType

  override def compareTo(value: Value[Long]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue.toString

  override def getContext: Option[ContextContent] = context
}

object TLangLong extends TLangType {
  override def getType: String = "Long"
}
