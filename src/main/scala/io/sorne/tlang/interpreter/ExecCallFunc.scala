package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperCallFuncObject, HelperFunc, HelperStatement}

import scala.collection.mutable

object ExecCallFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[Value[_]]] = {
    val caller = statement.asInstanceOf[HelperCallFuncObject]


    context.functions.get(caller.name) match {
      case Some(value) =>
        val newContext = manageParameters(caller, value, context)
        ExecFunc.run(value, newContext)
      case None => ExecLibFunc.run(caller, context)
    }



    //      Context(vars, funcs)

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
              case Some(value) => vars.put(findParamName(param._2, attr._2, helperFunc), value)
              case None =>
            }
          }
        })
      })

    }
    Context(vars, funcs)
  }

  private def findParamName(curryPos: Int, paramPos: Int, helperFunc: HelperFunc): String = {
    helperFunc.currying.get(curryPos).attrs(paramPos).attr.getOrElse(paramPos.toString)
  }
}
