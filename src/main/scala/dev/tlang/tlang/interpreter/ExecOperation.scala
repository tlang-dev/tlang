package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value.TLangBool
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object ExecOperation extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val block = statement.asInstanceOf[Operation]
    //    solveConditionBlock(block, context) match {
    //      case Left(error) => Left(error)
    //      case Right(value) => Right(Some(List(new TLangBool(None, value))))
    //    }
    execOperation(block, context)
  }

  def execOperation(block: Operation, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val value = block.content match {
      case Left(op) => execOperation(op, context)
      case Right(value) => ExecComplexValue.run(value, context)
    }
    value match {
      case Left(err) => Left(err)
      case Right(value) =>
        if (block.next.isDefined) {
          flatten(block.next.get._2, ListBuffer.empty.addOne((None, block))) match {
            case Left(err) => Left(err)
            case Right(value) => execFlattenedOps(value, context)
          }
        } else Right(value)
    }

  }

  @tailrec
  def flatten(operation: Operation, ops: ListBuffer[(Option[Operator.operator], Operation)]): Either[ExecError, List[(Option[Operator.operator], Operation)]] = {
    if (operation.next.isDefined) {
      ops.addOne((Some(operation.next.get._1), operation.next.get._2))
      flatten(operation.next.get._2, ops)
    } else Right(ops.toList)
  }

  def execFlattenedOps(ops: List[(Option[Operator.operator], Operation)], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val valBuff = ListBuffer.empty[(Option[Operator.operator], Value[_])]
    var error: Option[ExecError] = None
    var i = 0
    while (i < ops.length && error.isEmpty) {
      ExecOperation.run(ops(i)._2, context) match {
        case Left(err) => error = Some(err)
        case Right(value) => valBuff.addOne((ops(i)._1, value.get.head))
      }
      i = i + 1
    }
    val values = valBuff.toList
    if (Operator.conditionals.contains(ops(1)._1.get)) {
      execCondition(values, 0)
    }
    else execMath(ops)
  }

  @tailrec
  def execCondition(ops: List[(Option[Operator.operator], Value[_])], level: Int): Either[ExecError, Option[List[Value[_]]]] = {
    if (ops.length > 1 && Operator.conditionPriority.length < level) {
      var i = 1
      var found = false
      while (i < ops.length && !found) {
        if (Operator.conditionPriority(level).contains(ops(i)._1.get)) {
          found = true
        } else i = i + 1
      }
      if (found) {
        compareLevel(ops(i - 1)._2.asInstanceOf[TLangBool], ops(i)._1.get, ops(i)._2.asInstanceOf[TLangBool], level) match {
          case Left(err) => Left(err)
          case Right(newVal) => execCondition(reduce(ops, i, newVal), level)
        }
      } else execCondition(ops, level + 1)
    } else Right(Some(List(ops.head._2)))
  }

  def compareLevel(value1: TLangBool, operator: Operator.operator, value2: TLangBool, level: Int): Either[ExecError, TLangBool] = {
    if (level == 0) {
      operator match {
        case Operator.EQUAL => Right(new TLangBool(None, value1.compareTo(value2) == 0))
        case Operator.GREATER => Right(new TLangBool(None, value1.compareTo(value2) > 0))
        case Operator.LESSER => Right(new TLangBool(None, value1.compareTo(value2) < 0))
        case Operator.GREATER_OR_EQUAL => Right(new TLangBool(None, value1.compareTo(value2) >= 0))
        case Operator.LESSER_OR_EQUAL => Right(new TLangBool(None, value1.compareTo(value2) <= 0))
        case Operator.NOT_EQUAL => Right(new TLangBool(None, value1.compareTo(value2) != 0))
      }
    } else {
      operator match {
        case Operator.AND => Right(new TLangBool(None, value1.asInstanceOf[TLangBool].getElement && value2.asInstanceOf[TLangBool].getElement))
        case Operator.OR => Right(new TLangBool(None, value1.asInstanceOf[TLangBool].getElement || value2.asInstanceOf[TLangBool].getElement))
      }
    }
  }

  def reduce(ops: List[(Option[Operator.operator], Value[_])], pos: Int, newVal: Value[_]): List[(Option[Operator.operator], Value[_])] = {
    val newList = ListBuffer.from(ops)
    newList.insert(pos, (ops(pos - 1)._1, newVal))
    newList.remove(pos + 1, 2)
    newList.toList
  }

  def execMath(ops: List[(Option[Operator.operator], Operation)]): Either[ExecError, Option[List[Value[Any]]]] = {
    Right(None)
  }

  //  def solveConditionBlock(block: Operation, context: Context): Either[ExecError, Boolean] = {
  //
  //    def callCondition(condition: Condition): Either[ExecError, Boolean] = {
  //      solveCondition(condition, context) match {
  //        case Left(error) => Left(error)
  //        case Right(res) => if (block.link.isDefined) {
  //          if (block.link.get == Operator.AND && !res) Right(false)
  //          else {
  //            solveConditionBlock(block.nextBlock.get, context) match {
  //              case Left(error) => Left(error)
  //              case Right(res2) =>
  //                if (res && res2) Right(true)
  //                else if (block.link.get == Operator.OR && (res || res2)) Right(true)
  //                else Right(false)
  //            }
  //          }
  //        } else Right(res)
  //      }
  //    }
  //
  //    block.content match {
  //      case Left(content) => solveConditionBlock(content, context)
  //      case Right(content) => callCondition(content)
  //    }
  //  }
  //
  //  def solveCondition(cond: Condition, context: Context): Either[ExecError, Boolean] = {
  //    execStatement(cond.statement1, context) match {
  //      case Left(error) => Left(error)
  //      case Right(res1) =>
  //        val state1 = res1.asInstanceOf[Value[Any]]
  //        if (cond.condition.isDefined) {
  //          execStatement(cond.statement2.get, context) match {
  //            case Left(error) => Left(error)
  //            case Right(res2) =>
  //              val state2 = res2.asInstanceOf[Value[Any]]
  //              if (state1.getType != state2.getType) Left(WrongType(state1.getType + " is of different type than " + state2.getType))
  //              else {
  //                val res: Boolean = cond.condition.get match {
  //                  case dev.tlang.tlang.ast.helper.ConditionType.EQUAL => state1.compareTo(state2) == 0
  //                  case dev.tlang.tlang.ast.helper.ConditionType.GREATER => state1.compareTo(state2) > 0
  //                  case dev.tlang.tlang.ast.helper.ConditionType.LESSER => state1.compareTo(state2) < 0
  //                  case dev.tlang.tlang.ast.helper.ConditionType.GREATER_OR_EQUAL => state1.compareTo(state2) >= 0
  //                  case dev.tlang.tlang.ast.helper.ConditionType.LESSER_OR_EQUAL => state1.compareTo(state2) <= 0
  //                  case dev.tlang.tlang.ast.helper.ConditionType.NOT_EQUAL => state1.compareTo(state2) != 0
  //                }
  //                if (cond.link.isDefined) solveNextCondition(res, cond.link.get, cond.nextBlock.get, context) else Right(res)
  //              }
  //          }
  //        } else {
  //          if (state1.getType == TLangBool.getType) {
  //            val res = state1.asInstanceOf[TLangBool]
  //            if (cond.link.isDefined) {
  //              solveNextCondition(res.getElement, cond.link.get, cond.nextBlock.get, context)
  //            } else Right(res.getElement)
  //          } else Left(WrongType("Should be Bool but is " + state1.getType))
  //        }
  //    }
  //  }
  //
  //  def solveNextCondition(state1: Boolean, cond: Operator.condition, next: ConditionBlock, context: Context): Either[ExecError, Boolean] = {
  //    if (!state1 && cond == Operator.AND) Right(false)
  //    else {
  //      solveConditionBlock(next, context) match {
  //        case Left(error) => Left(error)
  //        case Right(state2) =>
  //          if (state1 && state2) Right(true)
  //          else if (cond == Operator.OR && (state1 || state2)) Right(true)
  //          else Right(false)
  //      }
  //    }
  //  }
  //
  //  def execStatement(statement: HelperStatement, context: Context): Either[ExecError, Value[_]] = {
  //    ExecStatement.run(statement, context) match {
  //      case Left(error) => Left(error)
  //      case Right(value) => if (value.isDefined) {
  //        if (value.get.size == 1) Right(value.get.head)
  //        else Left(WrongNumberOfArguments("Expect 1 but found " + value.get.size))
  //      } else Left(WrongNumberOfArguments("Expect 1 but found 0"))
  //    }
  //  }
}
