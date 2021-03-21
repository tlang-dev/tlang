package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.operation.{Operation, Operator}
import dev.tlang.tlang.ast.common.value.TLangBool
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context

object ExecOperation extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val block = statement.asInstanceOf[Operation]
//    solveConditionBlock(block, context) match {
//      case Left(error) => Left(error)
//      case Right(value) => Right(Some(List(new TLangBool(None, value))))
//    }
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
