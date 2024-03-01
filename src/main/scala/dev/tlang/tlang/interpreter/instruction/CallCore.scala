package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core.Value
import tlang.core.func.FuncRet

import java.lang.reflect.Method
import scala.collection.mutable.ListBuffer

case class CallCore(className: String, methodName: String, totParams: Int) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val clazz = Class.forName(className)
    val method = clazz.getMethod(methodName, classOf[Value])
    val args = ListBuffer[Value]()
    for (i <- 0 until totParams) {
      args += state.getStack.pop()
    }
    val ret = invokeJavaMethodWithVarargs(method, args.toArray).asInstanceOf[FuncRet]
    state.getStack.push(ret)
    Right(())
  }

  private def invokeJavaMethodWithVarargs(method: Method, args: Array[AnyRef]): Any = {
    method.invoke(null, args: _*)
  }
}
