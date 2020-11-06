package io.sorne.tlang.interpreter

class ExecError(val code: String, val message: String = "")

case class CallableNotFound(name: String) extends ExecError(name, "Callable not found in context or inside another callable")

case class NotImplemented() extends ExecError("NotImplemented")

case class NotACondition() extends ExecError("NotACondition")

case class WrongType(types: String) extends ExecError("WrongType", types)

case class WrongNumberOfArguments(error: String) extends ExecError("WrongNumberOfArguments", error)
