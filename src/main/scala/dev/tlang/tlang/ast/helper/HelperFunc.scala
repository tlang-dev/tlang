package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope

case class HelperFunc(context: Option[ContextContent], name: String, currying: Option[List[HelperCurrying]] = None,
                      returns: Option[List[HelperParamType]] = None, block: HelperContent, scope: Scope = Scope()) extends HelperStatement with Value[HelperFunc] with AstContext {
  override def getElement: HelperFunc = this

  override def getType: String = HelperFunc.getType

  override def compareTo(value: Value[HelperFunc]): Int = this.name.compareTo(value.getElement.name)

  override def getContext: Option[ContextContent] = context
}

object HelperFunc extends TLangType {
  override def getType: String = "HelperFunc"
}
