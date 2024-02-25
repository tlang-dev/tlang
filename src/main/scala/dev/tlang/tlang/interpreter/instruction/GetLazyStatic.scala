package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.Lazy

case class GetLazyStatic(id: String, pos: Int) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val value = state.getStaticBox(id).getAt(pos)
    value match {
      case value1: Lazy =>
        state.getStack.push(value1.getValue)
      case _ =>
    }
    Right(())
  }
}
