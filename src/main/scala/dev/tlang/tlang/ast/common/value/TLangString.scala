package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}
import dev.tlang.tlang.tmpl.lang.ast.TmplValueAst
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLangValue

class TLangString(context: Option[ContextContent], value: String) extends PrimitiveValue[String] with AstContext {
  override def getElement: String = value

  override def getType: String = TLangString.getType

  override def compareTo(value: Value[String]): Int = this.value.compareTo(value.getElement)

  override def toString: String = getElement

  override def getContext: Option[ContextContent] = context

  override def add(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Right(new TLangString(None, this.value + value.getElement))

  override def subtract(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[String]): Either[ExecError, TLangString] = Left(NotImplemented(context = context))

  override def deepCopy(): TLangString = new TLangString(context, new String(value))

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langString.name)),
    Some(List())
  )
}

object TLangString extends TLangType {
  override def getType: String = "String"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
