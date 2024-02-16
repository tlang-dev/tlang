package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncParam, EmbeddedValue, SetAttribute}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.set.{ModelSetRef, ModelSetRefCurrying, ModelSetRefValue}
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import tlang.core.{Null, Value}

import scala.collection.mutable.ListBuffer

object ExecModelSetRef extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val ref = statement.asInstanceOf[ModelSetRef]
    if (ref.func.isDefined) {
      if (ref.currying.isEmpty) ExecCallRefFunc.runCallFunc(ref.func, None, context)
      else {
        mapCurrying(ref.currying.get, context) match {
          case Left(error) => Left(error)
          case Right(curry) => ExecCallRefFunc.runCallFunc(ref.func, Some(curry), context)
        }
      }
    } else {
      ContextUtils.findVar(context, ref.refs.mkString("/")) match {
        case Some(value) => Right(Some(List(value)))
        case None => Left(CallableNotFound(ref.refs.mkString("/"), ref.context))
      }
    }
  }

  def mapCurrying(curry: List[ModelSetRefCurrying], context: Context): Either[ExecError, List[CallFuncParam]] = {
    var i = 0
    var error: Option[ExecError] = None
    val curries = ListBuffer.empty[CallFuncParam]
    while (i < curry.size && error.isEmpty) {
      mapSetRefCurrying(curry(i), context) match {
        case Left(err) => error = Some(err)
        case Right(value) => curries.addOne(value)
      }
      i = i + 1
    }
    if (error.isDefined) Left(error.get)
    else Right(curries.toList)
  }

  def mapSetRefCurrying(curry: ModelSetRefCurrying, context: Context): Either[ExecError, CallFuncParam] = {
    if (curry.values.isEmpty) Right(CallFuncParam(curry.context, None))
    else {
      var i = 0
      var error: Option[ExecError] = None
      val params = ListBuffer.empty[SetAttribute]
      while (i < curry.values.size && error.isEmpty) {
        mapSetAttribute(curry.values(i), context) match {
          case Left(err) => error = Some(err)
          case Right(value) => params.addOne(value)
        }
        i = i + 1
      }
      if (error.isDefined) Left(error.get)
      else Right(CallFuncParam(curry.context, Some(params.toList)))
    }
  }

  def mapSetAttribute(param: ModelSetRefValue, context: Context): Either[ExecError, SetAttribute] = {
    param match {
      case setRef: ModelSetRef => ExecModelSetRef.run(setRef, context) match {
        case Left(err) => Left(err)
        case Right(value) =>
          if (value.isEmpty || value.get.size != 1) Left(WrongValueReturned("Ref should return one value", setRef.context))
          else Right(SetAttribute(setRef.context, None, Operation(setRef.context, None, Right(EmbeddedValue(setRef.context, value.get.head)))))
      }
      case operation: Operation => Right(SetAttribute(operation.context, None, operation))
      case _ => Left(NotImplemented("Should be a ref or an operation", Null.empty()))
    }
  }
}
