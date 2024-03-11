package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.Lazy

case class SetLazyStatic(id: String, pos: Int) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val lazyVal = state.getStaticBox(id).getAt(pos).asInstanceOf[Lazy]
    val value = state.getStack.pop()
    lazyVal.setValue(value)
    if (lazyVal.isInstanceOf[Lazy]) {
      state.getStaticBox(id).replaceAt(pos, lazyVal)
      state.getLogger.debug("SetLazyStatic: [" + id + "] replaced at " + pos)
    }
    Right(())
  }
}
