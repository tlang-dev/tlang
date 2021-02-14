package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallFuncParam, CallRefFuncObject, SetAttribute}
import dev.tlang.tlang.ast.common.value.LazyValue
import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}

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
          val newContext = manageTmplParameters(caller, tmpl, context)
          Right(Some(List(TmplBlockAsValue(tmpl.copy(), newContext))))
        case None => ContextUtils.findRefFunc(context, caller.name.get) match {
          case Some(refFunc) =>
            val newCaller = mergeCallers(caller, refFunc)
            ExecCallRefFunc.run(newCaller, context)
          //            refFunc.func.get match {
          //              case Left(func) =>
          //                val newCaller = mergeCallers(caller, refFunc)
          //                val newContext = manageParameters(newCaller, func, context)
          //                ExecFunc.run(func, newContext)
          //              case Right(tmpl) =>
          //                val newCaller = mergeCallers(caller, refFunc)
          //                val newContext = manageTmplParameters(newCaller, tmpl, context)
          //                Right(Some(List(TmplBlockAsValue(tmpl.copy(), newContext))))
          //            }
          case None => Left(CallableNotFound(caller.name.get))
        }
      }
    }
  }

  def manageParameters(caller: CallFuncObject, helperFunc: HelperFunc, context: Context): Context = {
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

  def findParamName(curryPos: Int, paramPos: Int, helperFunc: HelperFunc): String = {
    helperFunc.currying.get(curryPos).params(paramPos).param.getOrElse(paramPos.toString)
  }

  def manageTmplParameters(caller: CallFuncObject, tmpl: TmplBlock, context: Context): Context = {
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

  def findTmplParamName(paramPos: Int, tmplBlock: TmplBlock): String = {
    tmplBlock.params.get(paramPos).param.getOrElse(paramPos.toString)
  }

  def mergeCallers(funCaller: CallFuncObject, refFuncCaller: CallRefFuncObject): CallRefFuncObject = {
    if (funCaller.currying.isDefined || refFuncCaller.currying.isDefined) {
      val newCurry = ListBuffer.empty[CallFuncParam]
      if (refFuncCaller.currying.isDefined) {
        for (curry <- refFuncCaller.currying.get.zipWithIndex) {
          if (curry._1.params.isDefined) {
            val params = ListBuffer.empty[SetAttribute]
            var i = 0;
            for (param <- curry._1.params.get) {
              if (param.value.isInstanceOf[LazyValue[_]]) {
                params.addOne(funCaller.currying.get(curry._2).params.get(i))
                i += 1
              } else {
                params.addOne(param)
              }
            }
            newCurry.addOne(CallFuncParam(Some(params.toList)))
          } else newCurry.addOne(CallFuncParam(None))
        }
      }
      CallRefFuncObject(Some(newCurry.toList), refFuncCaller.func)
    } else CallRefFuncObject(None, refFuncCaller.func)
  }

}
