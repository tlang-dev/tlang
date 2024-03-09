package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.Lazy

case class SetLazyStatic(id: String, pos: Int) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val value = state.getStaticBox(id).getAt(pos)
//    val value = state.getBox.pop()
    if (value.isInstanceOf[Lazy]) {
      state.getStaticBox(id).replaceAt(pos, state.getStack.pop())
      state.getLogger.debug("SetLazyStatic: [" + id + "] replaced at " + pos)
    }
    Right(())
  }
}
