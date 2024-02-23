package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import tlang.core.{Bool, Null, Type}
import tlang.internal.{AstContext, ContextContent}

class TLangBool(context: Null[ContextContent], value: Bool) extends PrimitiveValue[Bool]() with AstContext {
  override def getElement: Bool = value

  override def getType: Type = TLangBool.getType

  override def toString: String = if (getElement.get()) "true" else "false"

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangBool.getType)),
    Some(List())
  )

  override def getContext: Null[ContextContent] = context
}

object TLangBool extends TLangType {
  override def getType: Type = Bool.TYPE

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
