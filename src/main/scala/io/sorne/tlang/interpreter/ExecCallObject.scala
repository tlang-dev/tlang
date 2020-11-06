package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.model.`new`._
import io.sorne.tlang.interpreter.context.{Context, ContextUtils}

import scala.annotation.tailrec

object ExecCallObject extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val arg1 = statement.asInstanceOf[HelperCallObject]
    loopOverStatement(arg1.statements, context) match {
      case Left(value) => Left(value)
      case Right(value) => Right(Some(List(value)))
    }
  }

  @tailrec
  private def loopOverStatement(statements: List[HelperCallObjectType], context: Context, index: Int = 0, callable: Option[Value[_]] = None): Either[ExecError, Value[_]] = {
    if (index >= statements.size) {
      if (callable.isDefined) Right(callable.get) else Left(NotImplemented())
    }
    else {
      if (callable.isDefined) {
        findInCallable(statements(index), callable.get) match {
          case Left(value) => Left(value)
          case Right(value) => loopOverStatement(statements, context, index + 1, Some(value))
        }
      }
      else {
        findInContext(statements(index), context) match {
          case Left(value) => Left(value)
          case Right(value) => loopOverStatement(statements, context, index + 1, Some(value))
        }
      }
    }
  }

  private def findInContext(statement: HelperCallObjectType, context: Context): Either[ExecError, Value[_]] = {
    statement match {
      case HelperCallArrayObject(name, position) => ContextUtils.findVar(context, name) match {
        case Some(array) => resolveCallback(position, array)
        case None => Left(CallableNotFound(name))
      }
      case HelperCallFuncObject(name, _) => ContextUtils.findFunc(context, name.get) match {
        case Some(value) => resolveCallback(name.get, value)
        case None => Left(CallableNotFound(name.get))
      }
      case HelperCallVarObject(name) => ContextUtils.findVar(context, name) match {
        case Some(value) => Right(value)
        case None => Left(CallableNotFound(name))
      }
      case _ => Left(NotImplemented())
    }
  }

  private def findInCallable(statement: HelperCallObjectType, callable: Value[_]): Either[ExecError, Value[_]] = {
    statement match {
      case HelperCallArrayObject(name, position) => resolveCallback(position, callable)
      case HelperCallFuncObject(name, _) => resolveCallback(name.get, callable)
      case HelperCallVarObject(name) => resolveCallback(name, callable)
      case _ => Left(NotImplemented())
    }
  }

  def resolveCallback(name: String, callable: Value[_]): Either[ExecError, Value[_]] = {
    callable match {
      case valueType: ModelNewArrayValue => resolveArray(name, valueType)
      case valueType: ModelNewCallFuncValue => resolveFunc(name, valueType)
      case valueType: ModelNewEntityValue => resolveEntity(name, valueType)
      case _ => Right(callable)
    }
  }

  def resolveArray(position: String, arrayValue: ModelNewArrayValue): Either[ExecError, Value[_]] = {
    arrayValue.tbl match {
      case Some(array) =>
        if (position.toIntOption.isDefined) Right(array(position.toInt).value)
        else {
          val callRes = array.find(elem => elem.attr.isDefined && elem.attr.get.equals(position))
          if (callRes.isDefined) Right(callRes.get.value)
          else Left(CallableNotFound(position))
        }
      case None => Left(CallableNotFound(position))
    }
  }

  def resolveEntity(name: String, entity: ModelNewEntityValue): Either[ExecError, Value[_]] = {
    if (entity.params.isDefined) findInEntity(name, entity.params.get)
    else if (entity.attrs.isDefined) findInEntity(name, entity.attrs.get)
    else Left(CallableNotFound(name))
  }

  def findInEntity(name: String, attrs: List[ModelNewAttribute]): Either[ExecError, Value[_]] = {
    attrs.find(_.attr.getOrElse(false).equals(name)) match {
      case Some(value) => Right(value.value)
      case None => Left(CallableNotFound(name))
    }
  }

  def resolveFunc(name: String, func: ModelNewCallFuncValue): Either[ExecError, Value[_]] = {
    Left(NotImplemented())
  }

}
