package dev.tlang.tlang.interpreter

import tlang.core.Null
import tlang.internal.ContextContent

class ExecError(val code: String, val message: String = "", context: Null[ContextContent]) {
  override def toString: String = {
    var msg = ""
    if (context.isNotNull.get()) msg += context.get.getResource.toString + "[line:" + context.get.getLine + ", pos:" + context.get.getCharPos + "]\n"
    msg += "[" + code + "] " + message
    msg
  }
}

case class CallableNotFound(name: String, context: Null[ContextContent]) extends ExecError(name, "Callable not found in context or inside another callable", context)

case class NotImplemented(msg: String = "", context: Null[ContextContent]) extends ExecError("NotImplemented", msg, context)

case class NotACondition(context: Null[ContextContent]) extends ExecError("NotACondition", context = context)

case class WrongType(types: String, context: Null[ContextContent]) extends ExecError("WrongType", types, context)

case class WrongNumberOfArguments(error: String, context: Null[ContextContent]) extends ExecError("WrongNumberOfArguments", error, context)

case class NoValue(error: String, context: Null[ContextContent]) extends ExecError("NoValue", error, context)

case class ElementNotFound(error: String, context: Null[ContextContent]) extends ExecError("ElementNotFound", error, context)

case class WrongValueReturned(msg: String, context: Null[ContextContent]) extends ExecError("WrongValueReturned", msg, context)

