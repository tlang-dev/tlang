package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

class TLangBool(context: Option[ContextContent], value: Boolean) extends PrimitiveValue[Boolean]() with AstContext {
  override def getValue: Boolean = value

  override def getType: String = TLangBool.getType

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getValue)

  override def toString: String = if (getValue) "true" else "false"

  override def getContext: Option[ContextContent] = context
}

object TLangBool extends TLangType {
  override def getType: String = "Bool"
}
