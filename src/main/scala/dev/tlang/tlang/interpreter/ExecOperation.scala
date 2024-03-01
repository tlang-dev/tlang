package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value.{PrimitiveValue, TLangBool}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.{Bool, Null, Value}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object ExecOperation extends Executor {

  override def run(statement: HelperStatement, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    val block = statement.asInstanceOf[Operation]
    execOperation(block, context, newLevel = true)
  }

  private def execOperation(block: Operation, context: AstContext, newLevel: Boolean): Either[ExecError, Option[List[Value]]] = {
    val value = block.content match {
      case Left(op) => execOperation(op, context, newLevel = true)
      case Right(value) => ExecComplexValue.run(value, context)
    }
    value match {
      case Left(err) =>
        Left(err)
      case Right(value) =>
        if (newLevel && block.next.isDefined) {
          flatten(block.next.get._1, block.next.get._2, ListBuffer.empty.addOne((None, value.get.head)), context) match {
            case Left(err) => Left(err)
            case Right(value) => applyOperation(value, 0)
          }
        } else Right(value)
    }

  }

  //  @tailrec
  //  def execOperationWithoutNext(block: Operation, context: Context): Either[ExecError, Option[List[Value]]] = {
  //
  //  }

  @tailrec
  private def flatten(operator: Operator.operator, operation: Operation, ops: ListBuffer[(Option[Operator.operator], Value)], context: AstContext): Either[ExecError, List[(Option[Operator.operator], Value)]] = {
    execOperation(operation, context, newLevel = false) match {
      case Left(err) => Left(err)
      case Right(value) =>
        ops.addOne(Some(operator), value.get.head)
        if (operation.next.isDefined) flatten(operation.next.get._1, operation.next.get._2, ops, context)
        else Right(ops.toList)
    }
  }

  @tailrec
  private def applyOperation(ops: List[(Option[Operator.operator], Value)], level: Int): Either[ExecError, Option[List[Value]]] = {
    if (ops.length > 1 && level < Operator.priorities.length) {
      var i = 1
      var found = false
      while (i < ops.length && !found) {
        if (Operator.priorities(level).contains(ops(i)._1.get)) {
          found = true
        } else i = i + 1
      }
      if (found) {
        compareLevel(ops(i - 1)._2, ops(i)._1.get, ops(i)._2, level) match {
          case Left(err) => Left(err)
          case Right(newVal) => applyOperation(reduce(ops, i, newVal), level)
        }
      } else applyOperation(ops, level + 1)
    } else Right(Some(List(ops.head._2)))
  }

  private def compareLevel(value1: Value, operator: Operator.operator, value2: Value, level: Int): Either[ExecError, Value] = {
//    level match {
//      case 0 =>
//        val val1 = value1.asInstanceOf[PrimitiveValue[Any]]
//        val val2 = value2.asInstanceOf[PrimitiveValue[Any]]
//        operator match {
//          case Operator.MULTIPLY => val1.multiply(val2)
//          case Operator.DIVIDE => val1.divide(val2)
//          case Operator.MODULO => val1.modulo(val2)
//        }
//      case 1 =>
//        val val1 = value1.asInstanceOf[PrimitiveValue[Any]]
//        val val2 = value2.asInstanceOf[PrimitiveValue[Any]]
//        operator match {
//          case Operator.ADD => val1.add(val2)
//          case Operator.SUBTRACT => val1.subtract(val2)
//        }
//      case 2 =>
//        val val1 = value1.asInstanceOf[Value[Any]]
//        val val2 = value2.asInstanceOf[Value[Any]]
//        operator match {
//          case Operator.EQUAL =>
//            Right(new TLangBool(Null.empty(), new Bool(val1.compareTo(val2).get() == 0)))
//          case Operator.GREATER => Right(new TLangBool(Null.empty(), new Bool(val1.compareTo(val2).get() > 0)))
//          case Operator.LESSER => Right(new TLangBool(Null.empty(), new Bool(val1.compareTo(val2).get() < 0)))
//          case Operator.GREATER_OR_EQUAL => Right(new TLangBool(Null.empty(), new Bool(val1.compareTo(val2).get() >= 0)))
//          case Operator.LESSER_OR_EQUAL => Right(new TLangBool(Null.empty(), new Bool(val1.compareTo(val2).get() <= 0)))
//          case Operator.NOT_EQUAL => Right(new TLangBool(Null.empty(), new Bool(val1.compareTo(val2).get() != 0)))
//        }
//      case 3 =>
//        operator match {
//          case Operator.AND => Right(new TLangBool(Null.empty(), new Bool(value1.asInstanceOf[TLangBool].getElement.get() && value2.asInstanceOf[TLangBool].getElement.get())))
//          case Operator.OR => Right(new TLangBool(Null.empty(), new Bool(value1.asInstanceOf[TLangBool].getElement.get() || value2.asInstanceOf[TLangBool].getElement.get())))
//        }
//    }
    Right(Null.empty())
  }

  //  def checkMathResult(res: Either[ExecError, Value[Any]]): Either[ExecError, Value] = {
  //    res match {
  //      case Left(err) => Left(err)
  //      case Right(value) => Right(value)
  //    }
  //  }

  private def reduce(ops: List[(Option[Operator.operator], Value)], pos: Int, newVal: Value): List[(Option[Operator.operator], Value)] = {
    val newList = ListBuffer.from(ops)
    newList.insert(pos - 1, (ops(pos - 1)._1, newVal))
    newList.remove(pos, 2)
    newList.toList
  }

}
