package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallFuncParam, CallRefFuncObject, SetAttribute}
import dev.tlang.tlang.ast.common.value.{ArrayValue, TLangLong}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import dev.tlang.tlang.interpreter.{ExecCallFunc, ExecError, Value}

import scala.collection.mutable.ListBuffer

object TmplFor {

  def tmplForFunc: HelperFunc = HelperFunc(None, "forEach", Some(List(HelperCurrying(None, List(HelperParam(None, Some("array"), HelperObjType(None, ArrayValue.getType)), HelperParam(None, Some("refFunc"), HelperObjType(None, CallRefFuncObject.getType)))))), None, HelperContent(None, Some(List(
    HelperInternalFunc((context: Context) => {
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

  private def runFor(context: Context, arrayVal: ArrayValue): Either[ExecError, Option[List[Value[_]]]] = {
    if (arrayVal.tbl.isDefined) {
      val array = arrayVal.tbl.get
      val newScope = Scope()
      val newContext = Context(context.scopes :+ newScope)
      val rets = ListBuffer.empty[Value[_]]
      var error: Option[ExecError] = None
      for (i <- array.indices) {
        newScope.variables.update("_i", new TLangLong(None, i))
        //        newScope.variables.update("_", array(i).value)
        val newCaller = CallFuncObject(None, Some("refFunc"), Some(List(CallFuncParam(None, Some(List(SetAttribute(None, Some("_"), array(i).value)))))))
        ExecCallFunc.run(newCaller, newContext) match {
          case Left(err) => error = Some(err)
          case Right(value) => value match {
            case Some(value) => rets.addOne(value.head)
            case None =>
          }
        }
      }
      if (error.isDefined) Left(error.get)
      else Right(Some(rets.toList))
    } else Right(None)
  }
}
