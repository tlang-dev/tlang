package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{ConditionLink, HelperCondition, HelperConditionBlock, HelperStatement}
import io.sorne.tlang.interpreter.`type`.TLangBool
import io.sorne.tlang.interpreter.context.Context

object ExecConditionBlock extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val block = statement.asInstanceOf[HelperConditionBlock]
    solveConditionBlock(block, context) match {
      case Left(error) => Left(error)
      case Right(value) => Right(Some(List(new TLangBool(value))))
    }
  }

  def solveConditionBlock(block: HelperConditionBlock, context: Context): Either[ExecError, Boolean] = {
    solveCondition(block.content, context) match {
      case Left(error) => Left(error)
      case Right(res) => if (block.link.isDefined) {
        if (block.link.get == ConditionLink.AND && !res) Right(false)
        else {
          solveConditionBlock(block.nextBlock.get, context) match {
            case Left(error) => Left(error)
            case Right(res2) =>
              if (res && res2) Right(true)
              else if (block.link.get == ConditionLink.OR && (res || res2)) Right(true)
              else Right(false)
          }
        }
      } else Right(res)
    }
  }

  def solveCondition(cond: HelperCondition, context: Context): Either[ExecError, Boolean] = {
    execStatement(cond.statement1, context) match {
      case Left(error) => Left(error)
      case Right(res1) =>
        val state1 = res1.asInstanceOf[Value[Any]]
        if (cond.condition.isDefined) {
          execStatement(cond.statement1, context) match {
            case Left(error) => Left(error)
            case Right(res2) =>
              val state2 = res2.asInstanceOf[Value[Any]]
              if (state1.getType != state2.getType) Left(WrongType(state1.getType + " is of different type than " + state2.getType))
              else {
                val res: Boolean = cond.condition.get match {
                  case io.sorne.tlang.ast.helper.ConditionType.EQUAL => state1.compareTo(state2) == 0
                  case io.sorne.tlang.ast.helper.ConditionType.GREATER => state1.compareTo(state2) > 0
                  case io.sorne.tlang.ast.helper.ConditionType.LESSER => state1.compareTo(state2) < 0
                  case io.sorne.tlang.ast.helper.ConditionType.GREATER_OR_EQUAL => state1.compareTo(state2) >= 0
                  case io.sorne.tlang.ast.helper.ConditionType.LESSER_OR_SMALLER => state1.compareTo(state2) <= 0
                  case io.sorne.tlang.ast.helper.ConditionType.NOT_EQUAL => state1.compareTo(state2) != 0
                }
                if (cond.link.isDefined) solveNextCondition(res, cond.link.get, cond.nextCondition.get, context) else Right(res)
              }
          }
        } else {
          if (state1.getType == TLangBool.getType) {
            val res = state1.asInstanceOf[TLangBool]
            if (cond.link.isDefined) {
              solveNextCondition(res.getValue, cond.link.get, cond.nextCondition.get, context)
            } else Right(res.getValue)
          } else Left(WrongType("Should be Bool but is " + state1.getType))
        }
    }
  }

  def solveNextCondition(state1: Boolean, cond: ConditionLink.condition, next: HelperCondition, context: Context): Either[ExecError, Boolean] = {
    if (!state1 && cond == ConditionLink.AND) Right(false)
    else {
      solveCondition(next, context) match {
        case Left(error) => Left(error)
        case Right(state2) =>
          if (state1 && state2) Right(true)
          else if (cond == ConditionLink.OR && (state1 || state2)) Right(true)
          else Right(false)
      }
    }
  }

  def execStatement(statement: HelperStatement, context: Context): Either[ExecError, Value[_]] = {
    ExecStatement.run(statement, context) match {
      case Left(error) => Left(error)
      case Right(value) => if (value.isDefined) {
        if (value.get.size == 1) Right(value.get.head)
        else Left(WrongNumberOfArguments("Expect 1 but found " + value.get.size))
      } else Left(WrongNumberOfArguments("Expect 1 but found 0"))
    }
  }
}
