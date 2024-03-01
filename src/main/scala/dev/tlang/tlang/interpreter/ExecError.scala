package dev.tlang.tlang.interpreter

import tlang.internal.ContextContent

class ExecError(val code: String, val message: String = "", context: Option[ContextContent]) {
  override def toString: String = {
    var msg = ""
    if (context.isDefined) msg += context.get.getValue.getResource.toString + "[line:" + context.get.getValue.getLine + ", pos:" + context.get.getValue.getValue.getCharPos + "]\n"
    msg += "[" + code + "] " + message
    msg
  }
}

case class CallableNotFound(name: String, context: Option[ContextContent]) extends ExecError(name, "Callable not found in context or inside another callable", context)

case class NotImplemented(msg: String = "", context: Option[ContextContent]) extends ExecError("NotImplemented", msg, context)

case class NotACondition(context: Option[ContextContent]) extends ExecError("NotACondition", context = context)

case class WrongType(types: String, context: Option[ContextContent]) extends ExecError("WrongType", types, context)

case class WrongNumberOfArguments(error: String, context: Option[ContextContent]) extends ExecError("WrongNumberOfArguments", error, context)

case class NoValue(error: String, context: Option[ContextContent]) extends ExecError("NoValue", error, context)

case class ElementNotFound(error: String, context: Option[ContextContent]) extends ExecError("ElementNotFound", error, context)

case class WrongValueReturned(msg: String, context: Option[ContextContent]) extends ExecError("WrongValueReturned", msg, context)

