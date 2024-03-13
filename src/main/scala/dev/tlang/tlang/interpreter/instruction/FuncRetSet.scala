package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.func.FuncRet

case class FuncRetSet() extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val value = state.getStack.pop()
    if (value.isInstanceOf[FuncRet]) state.getBox.set(value)
    else state.getBox.set(FuncRet.of(value))
    Right(())
  }
}
