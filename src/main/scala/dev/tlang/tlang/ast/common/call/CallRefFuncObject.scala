package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.tmpl.TmplBlock
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class CallRefFuncObject(context: Option[ContextContent], name: Option[String], currying: Option[List[CallFuncParam]], var func: Option[Either[HelperFunc, TmplBlock]] = None) extends CallObjectType with Value[CallRefFuncObject] with AstContext {
  override def getElement: CallRefFuncObject = this

  override def getType: String = CallRefFuncObject.getType

  override def compareTo(value: Value[CallRefFuncObject]): Int = 0

  override def getContext: Option[ContextContent] = context
}

object CallRefFuncObject extends TLangType {
  override def getType: String = "CallRefFuncObject"
}
