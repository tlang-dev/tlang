package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.interpreter.context.Scope
import tlang.core.{Array, Null, Value}
import tlang.internal.{AstContext, ContextContent}

case class HelperFunc(context: Null[ContextContent], name: String, currying: Option[List[HelperCurrying]] = None,
                      returns: Null[Array[ValueType]] = Null.empty(), block: HelperContent, scope: Scope = Scope()) extends HelperStatement with Value[HelperFunc] with AstContext {
  override def getElement: HelperFunc = this

  override def getType: String = HelperFunc.getType

  override def compareTo(value: Value[HelperFunc]): Int = this.name.compareTo(value.getElement.name)

  override def getContext: Null[ContextContent] = context
}

object HelperFunc extends TLangType {
  override def getType: String = "HelperFunc"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
