package dev.tlang.tlang.interpreter.instruction

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.State
import tlang.core

case class Comp(operator: Operator.operator) extends Instruction {
  override def run(state: State): Either[ExecError, Unit] = {
    val v1 = state.getStack.pop()
    val v2 = state.getStack.pop()
    v1 match {
      case long: core.Long if v2.isInstanceOf[core.Long] =>
        val res = compareLong(long.get(), v2.asInstanceOf[core.Long].get(), operator)
        state.getStack.push(new core.Bool(res))
      case double: core.Double if v2.isInstanceOf[core.Double] =>
        val res = compareDouble(double.get(), v2.asInstanceOf[core.Double].get(), operator)
        state.getStack.push(new core.Bool(res))
      case bool: core.Bool if v2.isInstanceOf[core.Bool] =>
        operator match {
          case Operator.EQUAL => state.getStack.push(new core.Bool(bool.get() == v2.asInstanceOf[core.Bool].get()))
          case Operator.NOT_EQUAL => state.getStack.push(new core.Bool(bool.get() != v2.asInstanceOf[core.Bool].get()))
          case _ =>
        }
      case _ =>
    }
    Right(())
  }

  private def compareLong(v1: Long, v2: Long, operator: Operator.operator): Boolean = {
    operator match {
      case Operator.EQUAL => v1 == v2
      case Operator.NOT_EQUAL => v1 != v2
      case Operator.GREATER => v1 > v2
      case Operator.GREATER_OR_EQUAL => v1 >= v2
      case Operator.LESSER => v1 < v2
      case Operator.LESSER_OR_EQUAL => v1 <= v2
      case _ => throw new Exception("Unknown operator")
    }
  }

  private def compareDouble(v1: Double, v2: Double, operator: Operator.operator): Boolean = {
    operator match {
      case Operator.EQUAL => v1 == v2
      case Operator.NOT_EQUAL => v1 != v2
      case Operator.GREATER => v1 > v2
      case Operator.GREATER_OR_EQUAL => v1 >= v2
      case Operator.LESSER => v1 < v2
      case Operator.LESSER_OR_EQUAL => v1 <= v2
      case _ => throw new Exception("Unknown operator")
    }
  }
}
