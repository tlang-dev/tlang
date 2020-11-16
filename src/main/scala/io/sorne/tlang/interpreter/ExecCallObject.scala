package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.helper.call.{HelperCallArrayObject, HelperCallFuncObject, HelperCallInt, HelperCallObject, HelperCallObjectType, HelperCallString, HelperCallVarObject}
import io.sorne.tlang.ast.model.`new`._
import io.sorne.tlang.interpreter.`type`.{TLangInt, TLangString}
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
        findInCallable(statements(index), callable.get, context) match {
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
        case Some(array) => resolveArray(position, array.asInstanceOf[ModelNewArrayValue], context)
        case None => Left(CallableNotFound(name))
      }
      case HelperCallFuncObject(name, _) => ContextUtils.findFunc(context, name.get) match {
        case Some(value) => resolveFunc(name.get, value.asInstanceOf[HelperFunc])
        case None => Left(CallableNotFound(name.get))
      }
      case HelperCallVarObject(name) => ContextUtils.findVar(context, name) match {
        case Some(value) => Right(value)
        case None => Left(CallableNotFound(name))
      }
      case HelperCallString(value) => Right(new TLangString(value))
      case HelperCallInt(value) => Right(new TLangInt(value))
      case _ => Left(NotImplemented())
    }
  }

  private def findInCallable(statement: HelperCallObjectType, callable: Value[_], context: Context): Either[ExecError, Value[_]] = {
    statement match {
      case HelperCallArrayObject(_, position) => resolveArray(position, callable.asInstanceOf[ModelNewArrayValue], context)
      case HelperCallFuncObject(name, _) => resolveFunc(name.get, callable.asInstanceOf[HelperFunc])
      case HelperCallVarObject(name) => resolveCallback(name, callable)
      case _ => Left(NotImplemented())
    }
  }

  //  def resolveCallBackForArray(name:String, position: HelperCallObject, callable:Value[_]): Either[ExecError, Value[_]] = {
  //
  //  }

  def resolveCallback(name: String, callable: Value[_]): Either[ExecError, Value[_]] = {
    callable match {
      //      case valueType: ModelNewArrayValue => resolveArray(name, valueType)
      //      case valueType: ModelNewCallFuncValue => resolveFunc(name, valueType)
      case valueType: ModelNewEntityValue => resolveEntity(name, valueType)
      case _ => Right(callable)
    }
  }

  def resolveArray(position: HelperCallObject, arrayValue: ModelNewArrayValue, context: Context): Either[ExecError, Value[_]] = {
    val posValue = loopOverStatement(position.statements, context)

    arrayValue.tbl match {
      case Some(array) => posValue match {
        case Left(error) => Left(error)
        case Right(value) => value match {
          case int: TLangInt => Right(array(int.getValue).value)
          case str: TLangString =>
            val callRes = array.find(elem => elem.attr.isDefined && elem.attr.get.equals(str.getValue))
            if (callRes.isDefined) Right(callRes.get.value)
            else Left(CallableNotFound(value.getValue.toString))
          case _ => Left(WrongType("Should be Int or String instead of " + value.getType))
        }
      }
      case None => Left(CallableNotFound("position"))
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

  def resolveFunc(name: String, func: HelperFunc): Either[ExecError, Value[_]] = {
    Left(NotImplemented())
  }

}
