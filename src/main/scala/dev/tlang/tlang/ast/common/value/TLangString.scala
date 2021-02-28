package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

class TLangString(context: Option[ContextContent], value: String) extends PrimitiveValue[String] with AstContext {
  override def getValue: String = value

  override def getType: String = TLangString.getType

  override def compareTo(value: Value[String]): Int = this.value.compareTo(value.getValue)

  override def toString: String = getValue

  override def getContext: Option[ContextContent] = context
}

object TLangString extends TLangType {
  override def getType: String = "String"
}
