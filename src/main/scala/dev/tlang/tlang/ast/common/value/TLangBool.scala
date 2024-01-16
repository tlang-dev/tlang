package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}

class TLangBool(context: Option[ContextContent], value: Boolean) extends PrimitiveValue[Boolean]() with AstContext {
  override def getElement: Boolean = value

  override def getType: String = TLangBool.getType

  override def compareTo(value: Value[Boolean]): Int = this.value.compareTo(value.getElement)

  override def toString: String = if (getElement) "true" else "false"

  override def getContext: Option[ContextContent] = context

  override def add(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def subtract(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[Boolean]): Either[ExecError, TLangBool] = Left(NotImplemented(context = context))

  override def deepCopy(): TLangBool = new TLangBool(context, value.booleanValue())

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langBool.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}

object TLangBool extends TLangType {
  override def getType: String = "Bool"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
