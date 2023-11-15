package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallFuncParam, CallRefFuncObject}
import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import dev.tlang.tlang.interpreter.context.{Context, MutableContext}

object ExecCallRefFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val caller = statement.asInstanceOf[CallRefFuncObject]
    runCallFunc(caller.func, caller.currying, context)
  }

  def runCallFunc(func: Option[Either[HelperFunc, TmplBlock]], currying: Option[List[CallFuncParam]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    func.get match {
      case Left(func) =>
        val newContext = ExecCallFunc.manageParameters(CallFuncObject(None, None, currying), func, MutableContext.toMutable(context).removeLocalScopes().toContext())
        ExecFunc.run(func, newContext)
      case Right(tmpl) =>
        val tmplCopy = tmpl.deepCopy()
        val newContext = ExecCallFunc.manageTmplParameters(CallFuncObject(None, None, currying), tmplCopy, MutableContext.toMutable(context).removeLocalScopes().toContext())
        Right(Some(List(TmplBlockAsValue(tmplCopy.context, tmplCopy, newContext))))
    }
  }
}
