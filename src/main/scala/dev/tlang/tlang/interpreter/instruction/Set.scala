package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State
import tlang.core.Value

case class Set(value: Option[Value] = None) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    if (value.isDefined) state.getBox.set(value.get)
    else state.getBox.set(state.getStack.load())
    Right(())
  }
}
