package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.interpreter.context.Scope
import tlang.core.{Null, Type, Value}
import tlang.internal.{Context, ContextContent}

case class HelperFunc(context: Null, name: String, currying: Option[List[HelperCurrying]] = None,
                      returns: Option[List[ValueType]] = None, block: HelperContent, scope: Scope = Scope()) extends HelperStatement with Value with Context {
  override def getValue: Value = this

  override def getType: Type = if (context.isNotNull.get()) ManualType(context.get().getValue.asInstanceOf[ContextContent].getResource.getPkg.toString, name) else HelperFunc.getType

  override def getContext: Null = context
}

object HelperFunc extends TLangType {
  override def getType: Type = ManualType(this.getClass.getPackageName, "HelperFunc")

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
