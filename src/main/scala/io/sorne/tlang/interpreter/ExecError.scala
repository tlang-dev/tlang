package io.sorne.tlang.interpreter

class ExecError(val code: String, val message: String = "")

case class CallableNotFound(name: String) extends ExecError(name, "Callable not found in context or inside another callable")

case class NotImplemented() extends ExecError("NotImplemented")
