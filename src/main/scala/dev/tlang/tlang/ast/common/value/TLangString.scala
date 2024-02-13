package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

class TLangString(context: Null[ContextContent], value: String) extends PrimitiveValue[String] with AstContext {
  override def getElement: String = value

  override def getType: String = TLangString.getType

  override def toString: String = getElement

  override def add(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Right(new TLangString(Null.empty(), this.value + value.getElement))

  override def subtract(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def deepCopy(): TLangString = new TLangString(context, new String(value))

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangString.getType)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
}

object TLangString extends TLangType {

  override def getType: String = "String"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
