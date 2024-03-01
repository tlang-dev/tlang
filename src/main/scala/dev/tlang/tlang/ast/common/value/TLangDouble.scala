package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstValue}
import tlang.core
import tlang.core.Type
import tlang.internal.ContextContent

class TLangDouble(context: Option[ContextContent], value: Double) extends PrimitiveValue[Double] {

  override def getType: Type = TLangDouble.getType

  override def toString: String = getElement.toString

  //  override def deepCopy(): TLangDouble = new TLangDouble(context, value)

  override def toEntity: AstEntity = AstEntity(context,
    Some(toModel),
    Some(List())
  )

  override def toModel: AstModel = AstModel(None, getType, Some(LangModel.langNode), None, Some(List(
  )))

  //  override def toModel: Model = ???

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def getElement: TLangDouble = this

  override def getValue: Double = value
}

object TLangDouble extends TLangType {
  override def getType: Type = core.Double.TYPE

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
