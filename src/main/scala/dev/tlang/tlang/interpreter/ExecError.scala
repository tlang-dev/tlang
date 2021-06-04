package dev.tlang.tlang.interpreter

import dev.tlang.tlang.astbuilder.context.ContextContent

class ExecError(val code: String, val message: String = "", context: Option[ContextContent] = None) {
  override def toString: String = "[" + code + "] " + message
}

case class CallableNotFound(name: String, context: Option[ContextContent] = None) extends ExecError(name, "Callable not found in context or inside another callable", context)

case class NotImplemented(msg: String = "", context: Option[ContextContent] = None) extends ExecError("NotImplemented", msg, context)

case class NotACondition(context: Option[ContextContent] = None) extends ExecError("NotACondition", context = context)

case class WrongType(types: String, context: Option[ContextContent] = None) extends ExecError("WrongType", types, context)

case class WrongNumberOfArguments(error: String, context: Option[ContextContent] = None) extends ExecError("WrongNumberOfArguments", error, context)

case class NoValue(error: String, context: Option[ContextContent] = None) extends ExecError("NoValue", error, context)

case class ElementNotFound(error: String, context: Option[ContextContent] = None) extends ExecError("ElementNotFound", error, context)

case class WrongValueReturned(msg:String, context: Option[ContextContent] = None) extends ExecError("WrongValueReturned", msg, context)

