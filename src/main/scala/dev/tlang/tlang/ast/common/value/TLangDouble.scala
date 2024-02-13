package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core
import tlang.core.{Int, Null, Value}
import tlang.internal.ContextContent

class TLangDouble(context: Null[ContextContent], value: core.Double) extends PrimitiveValue[core.Double] {

  override def getType: String = TLangDouble.getType

  override def compareTo(value: Value[core.Double]): Int = new Int(this.value.get().compareTo(value.getElement.get()))

  override def toString: String = getElement.toString

  override def add(value: PrimitiveValue[core.Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(Null.empty(), new core.Double(this.value.get() + value.getElement)))

  override def subtract(value: PrimitiveValue[core.Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(Null.empty(), new core.Double(this.value.get() - value.getElement)))

  override def multiply(value: PrimitiveValue[core.Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(Null.empty(), new core.Double(this.value.get() * value.getElement)))

  override def divide(value: PrimitiveValue[core.Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(Null.empty(), new core.Double(this.value.get() / value.getElement)))

  override def modulo(value: PrimitiveValue[core.Double]): Either[ExecError, TLangDouble] = Right(new TLangDouble(Null.empty(), new core.Double(this.value.get() % value.getElement)))

  override def deepCopy(): TLangDouble = new TLangDouble(context, value)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangDouble.getType)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(Null.empty(), getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))

  override def getElement: TLangDouble = this
}

object TLangDouble extends TLangType {
  override def getType: String = "Double"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
