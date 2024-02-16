package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

class TLangLong(context: Null[ContextContent], value: core.Long) extends PrimitiveValue[core.Long] with AstContext {
  override def getElement: core.Long = value

  override def getType: String = TLangLong.getType

  override def toString: String = getElement.toString

  override def add(value: PrimitiveValue[core.Long]): Either[ExecError, TLangLong] = Right(new TLangLong(Null.empty(), new core.Long(this.value.get() + value.getElement.get())))

  override def subtract(value: PrimitiveValue[core.Long]): Either[ExecError, TLangLong] = Right(new TLangLong(Null.empty(), new core.Long(this.value.get() - value.getElement.get())))

  override def multiply(value: PrimitiveValue[core.Long]): Either[ExecError, TLangLong] = Right(new TLangLong(Null.empty(), new core.Long(this.value.get() * value.getElement.get())))

  override def divide(value: PrimitiveValue[core.Long]): Either[ExecError, TLangLong] = Right(new TLangLong(Null.empty(), new core.Long(this.value.get() / value.getElement.get())))

  override def modulo(value: PrimitiveValue[core.Long]): Either[ExecError, TLangLong] = Right(new TLangLong(Null.empty(), new core.Long(this.value.get() % value.getElement.get())))

  override def deepCopy(): TLangLong = new TLangLong(context, value)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangLong.getType)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(Null.empty(), getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))

  override def getContext: Null[ContextContent] = context
}

object TLangLong extends TLangType {
  override def getType: String = "Long"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
