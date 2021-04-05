package dev.tlang.tlang.interpreter

class ExecError(val code: String, val message: String = "") {
  override def toString: String = "[" + code + "] " + message
}

case class CallableNotFound(name: String) extends ExecError(name, "Callable not found in context or inside another callable")

case class NotImplemented() extends ExecError("NotImplemented")

case class NotACondition() extends ExecError("NotACondition")

case class WrongType(types: String) extends ExecError("WrongType", types)

case class WrongNumberOfArguments(error: String) extends ExecError("WrongNumberOfArguments", error)

case class NoValue(error: String) extends ExecError("NoValue", error)

case class ElementNotFound(error: String) extends ExecError("ElementNotFound", error)
