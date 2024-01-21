package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.{ExecError, Value}
import dev.tlang.tlang.tmpl.lang.ast.LangModel

class TLangDouble(context: Option[ContextContent], value: Double) extends PrimitiveValue[Double] {
  override def getElement: Double = value

  override def getType: String = TLangDouble.getType

  override def compareTo(value: Value[scala.Double]): Int = this.value.compareTo(value.getElement)

  override def toString: String = getElement.toString

  override def getContext: Option[ContextContent] = context

  override def add(value: PrimitiveValue[Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(None, this.value + value.getElement))

  override def subtract(value: PrimitiveValue[Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(None, this.value - value.getElement))

  override def multiply(value: PrimitiveValue[Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(None, this.value * value.getElement))

  override def divide(value: PrimitiveValue[Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(None, this.value / value.getElement))

  override def modulo(value: PrimitiveValue[Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(None, this.value % value.getElement))

  override def deepCopy(): TLangDouble = new TLangDouble(context, value)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangDouble.getType)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

object TLangDouble extends TLangType {
  override def getType: String = "Double"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
