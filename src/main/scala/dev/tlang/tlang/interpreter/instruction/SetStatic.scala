package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.Value

case class SetStatic(id: String, value: Option[Value[_]] = None) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    if (value.isDefined) state.getStaticBox(id).set(value.get)
    else state.getStaticBox(id).set(state.getStack.load())
    Right(())
  }
}
