package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.CallFuncObject
import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.ast.tmpl.TmplBlock
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import dev.tlang.tlang.ast.common.call.CallFuncObject
import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}

import scala.collection.mutable

object ExecCallFunc extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val caller = statement.asInstanceOf[CallFuncObject]

    ContextUtils.findFunc(context, caller.name.get) match {
      case Some(func) =>
        val newContext = manageParameters(caller, func, context)
        ExecFunc.run(func, newContext)
      case None => ContextUtils.findTmpl(context, caller.name.get) match {
        case Some(tmpl) =>
          val newContext = manageTmplParameters(caller, tmpl, context)
          Right(Some(List(TmplBlockAsValue(tmpl.copy(), newContext))))
        case None => Left(CallableNotFound(caller.name.get))
      }
    }

  }

  private def manageParameters(caller: CallFuncObject, helperFunc: HelperFunc, context: Context): Context = {
    val vars: mutable.Map[String, Value[_]] = mutable.Map()
    val funcs: mutable.Map[String, HelperFunc] = mutable.Map()
    if (caller.currying.isDefined) {
      caller.currying.get.zipWithIndex.foreach(param => {
        param._1.params.get.zipWithIndex.foreach(attr => {
          ExecStatement.run(attr._1.value, context) match {
            case Left(value) => //Left(value)
            case Right(optionVal) => optionVal match {
              case Some(value) => if (value.size == 1) vars.put(findParamName(param._2, attr._2, helperFunc), value.head)
              case None =>
            }
          }
        })
      })
    }
    Context(context.scopes :+ Scope(vars, funcs))
  }

  private def findParamName(curryPos: Int, paramPos: Int, helperFunc: HelperFunc): String = {
    helperFunc.currying.get(curryPos).params(paramPos).param.getOrElse(paramPos.toString)
  }

  private def manageTmplParameters(caller: CallFuncObject, tmpl: TmplBlock, context: Context): Context = {
    val vars: mutable.Map[String, Value[_]] = mutable.Map()
    val funcs: mutable.Map[String, HelperFunc] = mutable.Map()
    if (tmpl.params.isDefined) {
      for (param <- tmpl.params.get.zipWithIndex) {
        ExecStatement.run(caller.currying.get.head.params.get(param._2).value, context) match {
          case Left(_) =>
          case Right(optionVal) => optionVal match {
            case None =>
            case Some(value) => if (value.size == 1) vars.put(findTmplParamName(param._2, tmpl), value.head)
          }
        }
      }
    }
    Context(context.scopes :+ Scope(vars, funcs))
  }

  private def findTmplParamName(paramPos: Int, tmplBlock: TmplBlock): String = {
    tmplBlock.params.get(paramPos).param.getOrElse(paramPos.toString)
  }

}
