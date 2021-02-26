package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallRefFuncObject}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.tmpl.TmplBlockAsValue
import dev.tlang.tlang.interpreter.context.Context

object ExecCallRefFunc extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val caller = statement.asInstanceOf[CallRefFuncObject]
    caller.func.get match {
      case Left(func) =>
        val newContext = ExecCallFunc.manageParameters(CallFuncObject(None, caller.currying), func, context)
        ExecFunc.run(func, newContext)
      case Right(tmpl) =>
        val tmplCopy = tmpl.deepCopy()
        val newContext = ExecCallFunc.manageTmplParameters(CallFuncObject(None, caller.currying), tmplCopy, context)
        Right(Some(List(TmplBlockAsValue(tmplCopy, newContext))))
    }
  }
}
