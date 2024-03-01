package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstValue}
import tlang.core.{Bool, Type}
import tlang.internal.ContextContent

class TLangBool(context: Option[ContextContent], value: Boolean) extends PrimitiveValue[Boolean]() {

  override def getType: Type = TLangBool.getType

  override def toString: String = if (value) "true" else "false"

  override def toEntity: AstEntity = AstEntity(context,
    Some(toModel),
    Some(List())
  )

  override def getContext: Option[ContextContent] = context

  override def getElement: TLangBool = this

  override def toModel: AstModel = AstModel(None, getType, Some(LangModel.langNode), None, Some(List(
  )))

  override def getName: String = getClass.getSimpleName

  override def getValue: Boolean = value
}

object TLangBool extends TLangType {
  override def getType: Type = Bool.TYPE

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
