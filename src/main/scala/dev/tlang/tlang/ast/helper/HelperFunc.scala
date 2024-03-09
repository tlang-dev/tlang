package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AstValue
import tlang.core.Type
import tlang.internal.ContextContent

case class HelperFunc(context: Option[ContextContent], name: String, currying: Option[List[HelperCurrying]] = None,
                      returns: Option[List[ValueType]] = None, block: HelperContent, scope: Scope = Scope()) extends HelperStatement with AstValue {

  override def getType: Type = if (context.isDefined) ManualType(context.get.getType.getType.toString, name) else HelperFunc.getType

  override def getContext: Option[ContextContent] = context

  override def getElement: AstValue = this
}

object HelperFunc extends TLangType {
  override def getType: Type = ManualType(this.getClass.getPackageName, "HelperFunc")

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
