package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.call.HelperCallFuncObject
import io.sorne.tlang.ast.helper.{HelperFunc, HelperStatement}
import io.sorne.tlang.interpreter
import io.sorne.tlang.interpreter.context.{Context, ContextUtils, Scope}

import scala.collection.mutable

object ExecCallFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val caller = statement.asInstanceOf[HelperCallFuncObject]

    ContextUtils.findFunc(context, caller.name.get) match {
      case Some(func) =>
        val newContext = manageParameters(caller, func, context)
        ExecFunc.run(func, newContext)
      case None => Left(CallableNotFound(caller.name.get))
    }

  }

  private def manageParameters(caller: HelperCallFuncObject, helperFunc: HelperFunc, context: Context): Context = {
    val vars: mutable.Map[String, Value[_]] = mutable.Map()
    val funcs: mutable.Map[String, HelperFunc] = mutable.Map()
    if (caller.currying.isDefined) {
      caller.currying.get.zipWithIndex.foreach(param => {
        param._1.attrs.zipWithIndex.foreach(attr => {
          ExecStatement.run(attr._1, context) match {
            case Left(value) => Left(value)
            case Right(optionVal) => optionVal match {
              case Some(value) => if (value.size == 1) vars.put(findParamName(param._2, attr._2, helperFunc), value.head)
              case None =>
            }
          }
        })
      })

    }
    Context(List(Scope(vars, funcs)))
  }

  private def findParamName(curryPos: Int, paramPos: Int, helperFunc: HelperFunc): String = {
    helperFunc.currying.get(curryPos).params(paramPos).param.getOrElse(paramPos.toString)
  }
}
