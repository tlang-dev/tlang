package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.jvm.JvmCapsule
import dev.tlang.tlang.interpreter.value.{InterJVM, InterValue}
import tlang.core
import tlang.core.Value

import scala.collection.mutable.ListBuffer

case class CallJVM(value: InterJVM, methodName: String, totParams: Int) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val mapParams = ListBuffer[Value]()
    for (_ <- 0 until totParams) {
      mapParams += state.getStack.pop()
    }
    val args = mapParams.reverse.map {
      case interValue: InterValue => JvmCapsule(state.program, state.logger, state.getBox, state.staticBoxes, interValue)
      case arg => arg
    }

    val ret = value.callFunc(new core.String(methodName), new core.Array(args.toArray))
    state.getStack.push(ret)
    Right(())
  }

}
