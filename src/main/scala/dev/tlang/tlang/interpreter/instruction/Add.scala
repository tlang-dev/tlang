package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter._
import dev.tlang.tlang.interpreter.context.State
import tlang.core

case class Add() extends Instruction {


  override def run(state: State): Either[ExecError, Unit] = {
    val v1 = state.getBox().get()
    val v2 = state.getBox().get()
    v1 match {
      case str: core.String =>
        state.getBox().set(new core.String(v1.asInstanceOf[core.String].get() + v2.asInstanceOf[core.String].get()))
        Right(())
    }
  }
}
