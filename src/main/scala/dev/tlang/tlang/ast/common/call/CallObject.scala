package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class CallObject(context: Option[ContextContent], statements: List[CallObjectType]) extends SimpleValueStatement[CallObject] with AstContext {
  override def getValue: CallObject = this

  override def getType: String = CallObject.getType

  override def compareTo(value: Value[CallObject]): Int = 0

  override def getContext: Option[ContextContent] = context
}

object CallObject extends TLangType {
  override def getType: String = "CallObject"
}
