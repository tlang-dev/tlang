package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallFuncParam, CallRefFuncObject}
import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.interpreter.context.{Context, MutableContext}
import dev.tlang.tlang.tmpl.{AnyTmplInterpretedBlock, AstContext}
import dev.tlang.tlang.tmpl.lang.ast.LangBlockAsValue
import tlang.core.{Null, Value}

object ExecCallRefFunc extends Executor {
  override def run(statement: HelperStatement, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    val caller = statement.asInstanceOf[CallRefFuncObject]
    runCallFunc(caller.func, caller.currying, context)
  }

  def runCallFunc(func: Option[Either[HelperFunc, AnyTmplInterpretedBlock[_]]], currying: Option[List[CallFuncParam]], context: AstContext): Either[ExecError, Option[List[Value]]] = {
    func.get match {
      case Left(func) =>
//        val newContext = ExecCallFunc.manageParameters(CallFuncObject(None, None, currying), func, MutableContext.toMutable(context).removeLocalScopes().toContext())
        ExecFunc.run(func, context)
      case Right(tmpl) =>
        Right(None)
//        val tmplCopy = tmpl.deepCopy().asInstanceOf[AnyTmplInterpretedBlock[_]]
//        val newContext = ExecCallFunc.manageTmplParameters(CallFuncObject(Null.empty(), None, currying), tmplCopy, MutableContext.toMutable(context).removeLocalScopes().toContext())
//        Right(Some(List(LangBlockAsValue(tmplCopy.getContext, tmplCopy, newContext))))
    }
  }
}
