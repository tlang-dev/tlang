package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.common.call.CallFuncObject
import io.sorne.tlang.ast.helper.{HelperFunc, HelperStatement}
import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import io.sorne.tlang.interpreter.context.{Context, ContextUtils, Scope}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object ExecCallFunc extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val caller = statement.asInstanceOf[CallFuncObject]

    ContextUtils.findFunc(context, caller.name.get) match {
      case Some(func) =>
        val newContext = manageParameters(caller, func, context)
        ExecFunc.run(func, newContext)
      case None => ContextUtils.findTmpl(context, caller.name.get) match {
        case Some(tmpl) =>
          val params = if (tmpl.params.nonEmpty) manageTmplParameters(caller, tmpl, context) else List()
          Right(Some(List(TmplBlockAsValue(tmpl, params))))
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
            case Left(value) => Left(value)
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

  private def manageTmplParameters(caller: CallFuncObject, tmpl: TmplBlock, context: Context): List[Value[_]] = {
    val params = ListBuffer.empty[Value[_]]
    for (param <- tmpl.params.get.zipWithIndex) {
      ExecCallObject.run(caller.currying.get.head.params.get(param._2).value, context) match {
        case Left(_) =>
        case Right(value) => params.addOne(value.get.head)
      }
    }
    params.toList
  }

}
