package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

case class StartStaticBox(id: String) extends Instruction {

  private var isInit = false

  override def run(state: State): Either[ExecError, Unit] = {
    if (!isInit) {
      state.newStaticBox(id)
      state.newBox()
      isInit = true
    }
//    state.levels.staticBox += 1
    Right(())
  }
}
