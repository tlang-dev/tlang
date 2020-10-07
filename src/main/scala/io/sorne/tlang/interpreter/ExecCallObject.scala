package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.model.`new`._

object ExecCallObject extends Executor {

  override def run(statement: HelperStatement, context: Context): Option[Value[_]] = {
    val arg1 = statement.asInstanceOf[HelperCallObject]


    None
  }

  private def loopOverStatement(statements: List[HelperCallObjectType], context: Context, index: Int, callable: Option[Callable]): Option[Value[_]] = {
    if (index >= statements.size) None
    else {
      if (callable.isDefined)
      else findInContext(statements(index), context)

    }
  }

  private def findInContext(statement: HelperCallObjectType, context: Context): Either[ExecError, Callable] = {
    statement match {
      case HelperCallArrayObject(name) =>
      case HelperCallFuncObject(name) =>
      case HelperCallVariableObject(name) =>
      case _ =>
    }
  }

  private def findInCallable(statement: HelperCallObjectType, callable: Callable): Either[ExecError, Callable] = {
    statement match {
      case HelperCallArrayObject(name) => resolveCallback(name, callable)
      case HelperCallFuncObject(name) =>
      case HelperCallVariableObject(name) =>
      case _ => Left(CallableNotFound(name))
    }
  }

  def resolveCallback(name: String, callable: Callable): Either[CallableNotFound, ModelNewValueType] = {
    callable match {
      case valueType: ModelNewArrayValue => resolveArray(name, callable.asInstanceOf[ModelNewArrayValue])
      case valueType: ModelNewEntityAsValue =>
      case valueType: ModelNewPrimitiveValue =>
      case valueType: ModelNewCallObjectValue =>
      case _ =>
    }
  }

  def resolveArray(name: String, arrayValue: ModelNewArrayValue): Either[CallableNotFound, ModelNewValueType] = {
    arrayValue.tbl match {
      case Some(array) =>
        if (name.toIntOption.isDefined) Right(array(name.toInt).value)
        else {
          val callRes = array.find(elem => elem.attr.isDefined && elem.attr.get.equals(name))
          if (callRes.isDefined) Right(callRes.get.value)
          else Left(CallableNotFound(name))
        }
      case None => Left(CallableNotFound(name))
    }
  }

}
