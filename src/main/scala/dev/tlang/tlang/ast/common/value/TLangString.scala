package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

class TLangString(context: Null[ContextContent], value: String) extends PrimitiveValue[String] with AstContext {
  override def getElement: String = value

  override def getType: Type = TLangString.getType

  override def toString: String = getElement

//  override def deepCopy(): TLangString = new TLangString(context, new String(value))

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangString.getType)),
    Some(List())
  )

  override def getContext: Null[ContextContent] = context
}

object TLangString extends TLangType {

  override def getType: Type = core.String.TYPE

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
