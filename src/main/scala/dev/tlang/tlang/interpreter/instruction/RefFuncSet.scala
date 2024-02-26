package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.func.FuncRet

case class RefFuncSet() extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val value = state.getStack.pop()
    state.getBox.set(FuncRet.of(value))
    Right(())
  }
}
