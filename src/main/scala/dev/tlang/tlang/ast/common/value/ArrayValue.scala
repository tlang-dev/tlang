package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.ContextContent

case class ArrayValue(context: Option[ContextContent], tbl: Option[List[ComplexAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getElement: ArrayValue = this

  override def getType: Type = ArrayValue.getType

  override def toEntity: AstEntity = AstEntity(context,
    Some(ArrayValue.model),
    Some(List())
  )

  override def getContext: Option[ContextContent] = context

  override def getValue: ArrayValue = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = ArrayValue.model
}

object ArrayValue extends TLangType {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  override def getType: Type = ManualType(getClass.getPackageName, name)

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)

  val model: AstModel = AstModel(None, getType, Some(LangModel.langNode), None, Some(List(
  )))
}
