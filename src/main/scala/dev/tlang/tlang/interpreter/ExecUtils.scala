package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncParam, CallObject, CallRefFuncObject, SetAttribute}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetRef, ModelSetRefCurrying, ModelSetRefValue}
import dev.tlang.tlang.interpreter.context.{Context, Scope}

object ExecUtils {

  def modelRefToCallRefFunc(ref: ModelSetRef): CallRefFuncObject = {
    CallRefFuncObject(ref.context, Some(ref.refs.last), modelCurryToCallFuncParam(ref.currying), ref.func, ref.scope)
  }

  def modelCurryToCallFuncParam(currying: Option[List[ModelSetRefCurrying]]): Option[List[CallFuncParam]] = {
    if (currying.isEmpty) None
    else Some(currying.get.map(curry => {
      val params = if (curry.values.isEmpty) None
      else Some(curry.values.map(refValueToSetAttribute))
      CallFuncParam(curry.context, params)
    }))
  }

  def refValueToSetAttribute(value: ModelSetRefValue): SetAttribute = {
    value match {
      case entity: EntityValue => SetAttribute(entity.context, None, Operation(entity.context, None, Right(entity)))
      case ref: ModelSetRef => SetAttribute(ref.context, None, Operation(ref.context, None, Right(CallObject(ref.context, List(modelRefToCallRefFunc(ref))))))
      case op: Operation => SetAttribute(op.context, None, op)
    }
  }

}
