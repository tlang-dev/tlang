package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Bool, Int, Null, Value}
import tlang.internal.{AstContext, ContextContent}

class TLangBool(context: Null[ContextContent], value: Bool) extends PrimitiveValue[Bool]() with AstContext {
  override def getElement: Bool = value

  override def getType: String = TLangBool.getType

  override def compareTo(value: Value[Bool]): Int = new Int(this.value.get().compareTo(value.getElement.get()))

  override def toString: String = if (getElement.get()) "true" else "false"

  override def add(value: PrimitiveValue[Bool]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def subtract(value: PrimitiveValue[Bool]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[Bool]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[Bool]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[Bool]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def deepCopy(): TLangBool = new TLangBool(context, new Bool(value.get()))

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangBool.getType)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(Null.empty(), getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
}

object TLangBool extends TLangType {
  override def getType: String = "Bool"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
