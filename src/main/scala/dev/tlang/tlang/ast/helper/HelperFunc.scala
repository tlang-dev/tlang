package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.interpreter.context.Scope
import tlang.core.{Array, Null, Type, Value}
import tlang.internal.{AstContext, ContextContent}

case class HelperFunc(context: Null[ContextContent], name: String, currying: Option[List[HelperCurrying]] = None,
                      returns: Option[List[ValueType]] = None, block: HelperContent, scope: Scope = Scope()) extends HelperStatement with Value[HelperFunc] with AstContext {
  override def getElement: HelperFunc = this

  override def getType: Type = if (context.isNotNull.get()) ManualType(context.get().getElement.getResource.getPkg.toString, name) else HelperFunc.getType

  override def getContext: Null[ContextContent] = context
}

object HelperFunc extends TLangType {
  override def getType: Type = ManualType(this.getClass.getPackageName, "HelperFunc")

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
