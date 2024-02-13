package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import tlang.core
import tlang.core.{Int, Null}
import tlang.internal.{AstContext, ContextContent}

case class CallRefFuncObject(context: Null[ContextContent], name: Option[String], currying: Option[List[CallFuncParam]], var func: Option[Either[HelperFunc, AnyTmplInterpretedBlock[_]]] = None, scope: Scope = Scope()) extends CallObjectType with core.Value[CallRefFuncObject] with AstContext {
  override def getElement: CallRefFuncObject = this

  override def getType: String = CallRefFuncObject.getType

  override def compareTo(value: core.Value[CallRefFuncObject]): Int = new Int(0)

  override def getContext: Null[ContextContent] = context
}

object CallRefFuncObject extends TLangType {
  override def getType: String = "CallRefFuncObject"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
