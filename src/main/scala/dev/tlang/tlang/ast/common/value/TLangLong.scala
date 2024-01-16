package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.{ExecError, Value}

class TLangLong(context: Option[ContextContent], value: Long) extends PrimitiveValue[Long] with AstContext {
  override def getElement: Long = value

  override def getType: String = TLangLong.getType

  override def compareTo(value: Value[Long]): Int = this.value.compareTo(value.getElement)

  override def toString: String = getElement.toString

  override def getContext: Option[ContextContent] = context

  override def add(value: PrimitiveValue[Long]): Either[ExecError, TLangLong] = Right(new TLangLong(None, this.value + value.getElement))

  override def subtract(value: PrimitiveValue[Long]): Either[ExecError, TLangLong] = Right(new TLangLong(None, this.value - value.getElement))

  override def multiply(value: PrimitiveValue[Long]): Either[ExecError, TLangLong] = Right(new TLangLong(None, this.value * value.getElement))

  override def divide(value: PrimitiveValue[Long]): Either[ExecError, TLangLong] = Right(new TLangLong(None, this.value / value.getElement))

  override def modulo(value: PrimitiveValue[Long]): Either[ExecError, TLangLong] = Right(new TLangLong(None, this.value % value.getElement))

  override def deepCopy(): TLangLong = new TLangLong(context, value)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langLong.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}

object TLangLong extends TLangType {
  override def getType: String = "Long"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
