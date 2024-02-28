package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstValue}
import tlang.core
import tlang.core.Type
import tlang.internal.ContextContent

class TLangString(context: Option[ContextContent], value: String) extends PrimitiveValue[String] {

  override def getType: Type = TLangString.getType

  override def toString: String = value

  //  override def deepCopy(): TLangString = new TLangString(context, new String(value))

  override def toEntity: AstEntity = AstEntity(context, None, Some(List()))

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = AstModel(context, getType, None, Some(List()), Some(List()))

  override def getElement: AstValue = this
}

object TLangString extends TLangType {

  override def getType: Type = core.String.TYPE

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
