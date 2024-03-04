package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core
import tlang.core.{Array, Value}

import scala.collection.mutable.ListBuffer

case class CallNextFunc(name: String, totalArgs: Int) extends Instruction {

  override def run(state: State): Either[ExecError, Unit] = {
    val args = ListBuffer.empty[Value]
    for (i <- 0 until totalArgs) {
      args += state.getStack.pop()
    }
    val value = state.getStack.pop()
    val ret = value.callFunc(new core.String(name), new Array(args.toArray))
    state.getStack.push(ret)
    Right(())
  }
}
