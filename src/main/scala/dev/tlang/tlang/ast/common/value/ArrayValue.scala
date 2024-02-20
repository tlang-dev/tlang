package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Null, Type, Value}
import tlang.internal.ContextContent

case class ArrayValue(context: Null[ContextContent], tbl: Option[List[ComplexAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getElement: ArrayValue = this

  override def getType: Type = ArrayValue.getType

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

  override def toModel: ModelSetEntity = ModelSetEntity(Null.empty(), getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))

  override def getContext: Null[ContextContent] = context
}

object ArrayValue extends TLangType {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  override def getType: Type = ManualType(getClass.getPackageName, name)

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
