package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class ArrayValue(context: Null[ContextContent], tbl: Option[List[ComplexAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getElement: ArrayValue = this

  override def getType: String = ArrayValue.getType

  override def compareTo(value: Value[ArrayValue]): Int = 0

  override def add(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = {
    if (tbl.isEmpty && value.getElement.tbl.isEmpty) Right(ArrayValue(Null.empty(), None))
    else Right(ArrayValue(Null.empty(), Some(tbl.getOrElse(List()) ++: value.getElement.tbl.getOrElse(List()))))
  }

  override def subtract(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def deepCopy(): ArrayValue = ArrayValue(context, tbl)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, ArrayValue.getType)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
}

object ArrayValue extends TLangType {
  override def getType: String = "ArrayValue"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
