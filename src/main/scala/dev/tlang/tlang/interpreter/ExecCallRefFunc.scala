package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallFuncParam, CallRefFuncObject}
import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.interpreter.context.{Context, MutableContext}
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.lang.ast.LangBlockAsValue
import tlang.core.{Null, Value}

object ExecCallRefFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val caller = statement.asInstanceOf[CallRefFuncObject]
    runCallFunc(caller.func, caller.currying, context)
  }

  def runCallFunc(func: Option[Either[HelperFunc, AnyTmplInterpretedBlock[_]]], currying: Option[List[CallFuncParam]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    func.get match {
      case Left(func) =>
        val newContext = ExecCallFunc.manageParameters(CallFuncObject(Null.empty(), None, currying), func, MutableContext.toMutable(context).removeLocalScopes().toContext())
        ExecFunc.run(func, newContext)
      case Right(tmpl) =>
        val tmplCopy = tmpl.deepCopy().asInstanceOf[AnyTmplInterpretedBlock[_]]
        val newContext = ExecCallFunc.manageTmplParameters(CallFuncObject(Null.empty(), None, currying), tmplCopy, MutableContext.toMutable(context).removeLocalScopes().toContext())
        Right(Some(List(LangBlockAsValue(tmplCopy.getContext, tmplCopy, newContext))))
    }
  }
}
