package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.func.FuncRet

case class RefFuncGet() extends Instruction {

  override def run(state: State): Either[ExecError, Unit] = {
    val ret = state.getStack.pop().asInstanceOf[FuncRet]
    state.getStack.push(ret.get())
    Right(())
  }

}
