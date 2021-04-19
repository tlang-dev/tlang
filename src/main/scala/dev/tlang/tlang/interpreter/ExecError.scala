package dev.tlang.tlang.interpreter

import dev.tlang.tlang.astbuilder.context.ContextContent

class ExecError(val code: String, val message: String = "", context: Option[ContextContent]=None) {
  override def toString: String = "[" + code + "] " + message
}

case class CallableNotFound(name: String) extends ExecError(name, "Callable not found in context or inside another callable")

case class NotImplemented() extends ExecError("NotImplemented")

case class NotACondition() extends ExecError("NotACondition")

case class WrongType(types: String) extends ExecError("WrongType", types)

case class WrongNumberOfArguments(error: String) extends ExecError("WrongNumberOfArguments", error)

case class NoValue(error: String, context: Option[ContextContent]=None) extends ExecError("NoValue", error, context)

case class ElementNotFound(error: String) extends ExecError("ElementNotFound", error)
