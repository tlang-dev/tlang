package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.func.FuncRet
import tlang.core.{Array, Value}

case class CallCore(className: String, methodName: String, args: Array) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val clazz = Class.forName(className)
    val method = clazz.getMethod(methodName, classOf[Value])
    val ret = method.invoke(null, args.getValue.getRecords.head).asInstanceOf[FuncRet]
    state.getStack.push(ret)
    Right(())
  }
}
