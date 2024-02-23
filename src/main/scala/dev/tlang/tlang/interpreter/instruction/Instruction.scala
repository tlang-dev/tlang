package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State

trait Instruction {

  def run(state: State): Either[ExecError, Unit]

}
