package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.{JumpIndex, State}

case class CallOnce(operation: Operation, setIndex: JumpIndex, var getIndex: JumpIndex) extends Instruction with ExecJump {

  private var isCalled = false

  override def run(state: State): Either[ExecError, Unit] = {
    if (!isCalled) {
      state.jumpTo(setIndex)
      isCalled = true
    } else state.jumpTo(getIndex)
    Right(None)
  }
}
