package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core

case class CallNextVar(name: String) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val value = state.getStack.pop()
    val nextVal = value.getAttr(new core.String(name))
    state.getStack.push(nextVal)
    Right(())
  }
}
