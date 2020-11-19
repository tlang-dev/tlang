package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.helper.call.{HelperCallArrayObject, HelperCallFuncObject, HelperCallInt, HelperCallObject, HelperCallObjectType, HelperCallString, HelperCallVarObject}
import io.sorne.tlang.ast.model.let._
import io.sorne.tlang.interpreter.`type`.{TLangInt, TLangString}
import io.sorne.tlang.interpreter.context.{Context, ContextUtils, Scope}

import scala.annotation.tailrec
import scala.collection.mutable

object ExecCallObject extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val arg1 = statement.asInstanceOf[HelperCallObject]
    loopOverStatement(arg1.statements, context)
  }

  @tailrec
  private def loopOverStatement(statements: List[HelperCallObjectType], context: Context, index: Int = 0, callable: Option[List[Value[_]]] = None): Either[ExecError, Option[List[Value[_]]]] = {
    if (index >= statements.size) Right(callable)
    else {
      if (callable.isDefined) {
        findInCallable(statements(index), callable, context) match {
          case Left(value) => Left(value)
          case Right(value) => loopOverStatement(statements, context, index + 1, value)
        }
      }
      else {
        findInContext(statements(index), context) match {
          case Left(value) => Left(value)
          case Right(value) => loopOverStatement(statements, context, index + 1, value)
        }
      }
    }
  }

  private def findInContext(statement: HelperCallObjectType, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case HelperCallArrayObject(name, position) => ContextUtils.findVar(context, name) match {
        case Some(array) => resolveArray(position, array.asInstanceOf[ModelNewArrayValue], context)
        case None => Left(CallableNotFound(name))
      }
      case caller: HelperCallFuncObject => ExecCallFunc.run(caller, context)
      case HelperCallVarObject(name) => ContextUtils.findVar(context, name) match {
        case Some(value) => Right(Some(List(value)))
        case None => Left(CallableNotFound(name))
      }
      case HelperCallString(value) => Right(Some(List(new TLangString(value))))
      case HelperCallInt(value) => Right(Some(List(new TLangInt(value))))
      case _ => Left(NotImplemented())
    }
  }

  private def findInCallable(statement: HelperCallObjectType, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case HelperCallArrayObject(_, position) => resolveArrayInCallable(position, callable, context)
      case caller: HelperCallFuncObject => resolveFunc(caller, callable, context)
      case HelperCallVarObject(name) => resolveCallback(name, callable)
      case _ => Left(NotImplemented())
    }
  }

  def resolveCallback(name: String, callable: Option[List[Value[_]]]): Either[ExecError, Option[List[Value[_]]]] = {
    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case valueType: ModelNewEntityValue => resolveEntity(name, valueType)
        case _ => Right(callable)
      }
    }
  }

  def resolveArray(position: HelperCallObject, array: ModelNewArrayValue, context: Context): Either[ExecError, Option[List[Value[_]]]] = {

    def resolve(posValue: Value[_]): Either[ExecError, Option[List[Value[_]]]] = {
      array.tbl match {
        case Some(array) => posValue match {
          case int: TLangInt => Right(Some(List(array(int.getValue).value)))
          case str: TLangString =>
            val callRes = array.find(elem => elem.attr.isDefined && elem.attr.get.equals(str.getValue))
            if (callRes.isDefined) Right(Some(List(callRes.get.value)))
            else Left(CallableNotFound(posValue.getValue.toString))
          case _ => Left(WrongType("Should be Int or String instead of " + posValue.getType))

        }
        case None => Left(CallableNotFound("position"))
      }

    }

    loopOverStatement(position.statements, context) match {
      case Left(error) => Left(error)
      case Right(res) => pickFirst(res) match {
        case Left(err) => Left(err)
        case Right(value) => resolve(value)
      }
    }

  }

  def resolveArrayInCallable(position: HelperCallObject, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => resolveArray(position, value.asInstanceOf[ModelNewArrayValue], context)

    }
  }

  def resolveEntity(name: String, entity: ModelNewEntityValue): Either[ExecError, Option[List[Value[_]]]] = {
    if (entity.params.isDefined) findInEntity(name, entity.params.get)
    else if (entity.attrs.isDefined) findInEntity(name, entity.attrs.get)
    else Left(CallableNotFound(name))
  }

  def findInEntity(name: String, attrs: List[ModelNewAttribute]): Either[ExecError, Option[List[Value[_]]]] = {
    attrs.find(_.attr.getOrElse(false).equals(name)) match {
      case Some(value) => Right(Some(List(value.value)))
      case None => Left(CallableNotFound(name))
    }
  }

  def resolveFunc(caller: HelperCallFuncObject, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {

    def execFunc(func: HelperFunc): Either[ExecError, Option[List[Value[_]]]] = {
      val newName = "_call_" + caller.name.getOrElse("func")
      val newCaller = HelperCallFuncObject(Some(newName), caller.currying)
      execFuncWithCaller(newCaller, func)
    }

    def execFuncWithCaller(newCaller: HelperCallFuncObject, func: HelperFunc): Either[ExecError, Option[List[Value[_]]]] = {
      val newScope = Scope(functions = mutable.Map(newCaller.name.get -> func))
      val newContext = Context(context.scopes :+ newScope)
      ExecCallFunc.run(newCaller, newContext)
    }

    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case func: HelperFunc => execFunc(func)
        case ref: ModelLetRefFunc =>
          val newName = "_call_" + ref.func.name
          val newCaller = HelperCallFuncObject(Some(newName), ref.currying)
          execFuncWithCaller(newCaller, ref.func)
        case _ => Left(NotImplemented())
      }
    }
  }

  def pickFirst(callable: Option[List[Value[_]]]): Either[ExecError, Value[_]] = {
    if (callable.isDefined && callable.get.nonEmpty) Right(callable.get.head)
    else Left(CallableNotFound("It's empty"))
  }

}
