package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.{AnyTmplInterpretedBlock, AstContext}
import tlang.core
import tlang.core.{Int, Type, Value}
import tlang.internal.ContextContent

case class CallRefFuncObject(context: Option[ContextContent], name: Option[String], currying: Option[List[CallFuncParam]], var func: Option[Either[HelperFunc, AnyTmplInterpretedBlock[_]]] = None, scope: Scope = Scope()) extends CallObjectType with core.Value with AstContext {
  override def getValue: Value = this

  override def getType: Type = CallRefFuncObject.getType

  override def compareTo(value: core.Value): Int = new Int(0)

  override def getContext: Option[ContextContent] = context
}

object CallRefFuncObject extends TLangType {
  override def getType: Type = ManualType(this.getClass.getPackageName, this.getClass.getSimpleName.replace("$", ""))

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
