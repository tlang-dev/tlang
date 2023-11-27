package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplValueAst}

case class ArrayValue(context: Option[ContextContent], tbl: Option[List[ComplexAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getElement: ArrayValue = this

  override def getType: String = ArrayValue.getType

  override def compareTo(value: Value[ArrayValue]): Int = 0

  override def getContext: Option[ContextContent] = context

  override def add(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = {
    if (tbl.isEmpty && value.getElement.tbl.isEmpty) Right(ArrayValue(None, None))
    else Right(ArrayValue(None, Some(tbl.getOrElse(List()) ++: value.getElement.tbl.getOrElse(List()))))
  }

  override def subtract(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[ArrayValue]): Either[ExecError, ArrayValue] = Left(NotImplemented(context = context))

  override def deepCopy(): ArrayValue = ArrayValue(context, tbl)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langArray.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}

object ArrayValue extends TLangType {
  override def getType: String = "ArrayValue"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
