package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import tlang.core
import tlang.core.{Int, Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class CallRefFuncObject(context: Null[ContextContent], name: Option[String], currying: Option[List[CallFuncParam]], var func: Option[Either[HelperFunc, AnyTmplInterpretedBlock[_]]] = None, scope: Scope = Scope()) extends CallObjectType with core.Value[CallRefFuncObject] with AstContext {
  override def getElement: CallRefFuncObject = this

  override def getType: Type = CallRefFuncObject.getType

  override def compareTo(value: core.Value[CallRefFuncObject]): Int = new Int(0)

  override def getContext: Null[ContextContent] = context
}

object CallRefFuncObject extends TLangType {
  override def getType: Type = ManualType(this.getClass.getPackageName, this.getClass.getSimpleName.replace("$", ""))

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
