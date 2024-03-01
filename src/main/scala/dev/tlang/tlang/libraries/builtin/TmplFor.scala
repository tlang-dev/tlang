package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallRefFuncObject
import dev.tlang.tlang.ast.common.value.ArrayValue
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.ExecError
import dev.tlang.tlang.interpreter.context.ContextUtils
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.{Null, Value}

object TmplFor {

  def tmplForFunc: HelperFunc = HelperFunc(None, "forEach", Some(List(HelperCurrying(None, List(HelperParam(None, Some("array"), ObjType(None, None, ArrayValue.getType)), HelperParam(None, Some("refFunc"), ObjType(None, None, CallRefFuncObject.getType)))))), None, HelperContent(None, Some(List(
    HelperInternalFunc((context: AstContext) => {
      ContextUtils.findVar(context, "array") match {
        case Some(arrayVar) => ContextUtils.findRefFunc(context, "refFunc") match {
          case Some(caller) =>
            val arrayVal = arrayVar.asInstanceOf[ArrayValue]
            runFor(context, arrayVal)
          case None => Right(None)
        }
        case None => Right(None)
      }
    })
  ))))

  private def runFor(context: AstContext, arrayVal: ArrayValue): Either[ExecError, Option[List[Value]]] = {
    //    if (arrayVal.tbl.isDefined) {
    //      val array = arrayVal.tbl.get
    //      val newScope = Scope()
    ////      val newContext = Context(context.scopes :+ newScope)
    //      val rets = ListBuffer.empty[Value]
    //      var error: Option[ExecError] = None
    //      for (i <- array.indices) {
    ////        newScope.variables.update("_i", new TLangLong(Null.empty(), new Long(i)))
    //        val newCaller = CallFuncObject(None, Some("refFunc"), Some(List(CallFuncParam(None, Some(List(SetAttribute(None, Some("_"), array(i).value)))))))
    //        ExecCallFunc.run(newCaller, newContext) match {
    //          case Left(err) => error = Some(err)
    //          case Right(value) => value match {
    //            case Some(value) => rets.addOne(value.head)
    //            case None =>
    //          }
    //        }
    //      }
    //      if (error.isDefined) Left(error.get)
    //      else Right(Some(rets.toList))
    //    } else Right(None)
    Right(None)
  }
}
