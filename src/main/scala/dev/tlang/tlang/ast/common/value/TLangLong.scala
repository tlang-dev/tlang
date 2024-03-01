package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstValue}
import tlang.core
import tlang.core.Type
import tlang.internal.ContextContent

class TLangLong(context: Option[ContextContent], value: Long) extends PrimitiveValue[Long] {

  override def getType: Type = TLangLong.getType

  override def toString: String = getElement.toString

  //  override def deepCopy(): TLangLong = new TLangLong(context, value)

  override def toEntity: AstEntity = AstEntity(context,
    Some(toModel),
    Some(List())
  )

  override def toModel: AstModel = AstModel(None, getType, Some(LangModel.langNode), None, Some(List(
  )))

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def getElement: AstValue = this

  override def getValue: Long = value
}

object TLangLong extends TLangType {
  override def getType: Type = core.Long.TYPE

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
