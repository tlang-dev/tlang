package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallObjectType, CallVarObject, _}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.tmpl.TmplBlockAsValue
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}

import scala.annotation.tailrec

object ExecCallObject extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val arg1 = statement.asInstanceOf[CallObject]
    loopOverStatement(arg1.statements, context)
  }

  @tailrec
  private def loopOverStatement(statements: List[CallObjectType], context: Context, index: Int = 0, callable: Option[List[Value[_]]] = None): Either[ExecError, Option[List[Value[_]]]] = {
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
          case Left(value) =>
            if (statements.size >= 2 && statements.head.isInstanceOf[CallVarObject]) tryExternalResource(statements, context)
            else Left(value)
          case Right(value) => loopOverStatement(statements, context, index + 1, value)
        }
      }
    }
  }

  def tryExternalResource(statements: List[CallObjectType], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val callVar = statements.head.asInstanceOf[CallVarObject]
    statements(1) match {
      case func: CallFuncObject =>
        val name = callVar.name + "/" + func.name.get
        ContextUtils.findFunc(context, name) match {
          case Some(_) => ExecCallFunc.run(CallFuncObject(None, Some(name), func.currying), context)
          case None => ContextUtils.findTmpl(context, name) match {
            case Some(tmpl) =>
              val tmplCopy = tmpl.deepCopy()
              Right(Some(List(TmplBlockAsValue(tmplCopy.getContext, tmplCopy, Context(context.scopes :+ tmplCopy.scope)))))
            case None => Left(CallableNotFound(name))
          }
        }
      case variable: CallVarObject => findVar(callVar.name + "/" + variable.name, context)
      case refFunc: CallRefFuncObject => Right(Some(List(refFunc)))
    }
  }

  private def findInContext(statement: CallObjectType, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case CallArrayObject(None, name, position) => ContextUtils.findVar(context, name) match {
        case Some(array) => resolveArray(position, array.asInstanceOf[ArrayValue], context)
        case None => Left(CallableNotFound(name))
      }
      case caller: CallFuncObject => ExecCallFunc.run(caller, context)
      case refFunc: CallRefFuncObject => Right(Some(List(refFunc)))
      case CallVarObject(_, name) => findVar(name, context)
      case _ => Left(NotImplemented())
    }
  }

  private def findVar(name: String, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    ContextUtils.findVar(context, name) match {
      case Some(value) =>
        value match {
          case operation: Operation => ExecOperation.run(operation, context)
          case _ => Right(Some(List(value)))
        }
      case None => Left(CallableNotFound(name))
    }
  }

  private def findInCallable(statement: CallObjectType, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case callArray: CallArrayObject => resolveArrayInCallable(callArray, callable, context)
      //case caller: HelperCallFuncObject => resolveFunc(caller, callable, context)
      case refFunc: CallRefFuncObject => Right(Some(List(refFunc)))
      case CallVarObject(_, name) => resolveCallVar(name, callable, context)
      case _ => Left(NotImplemented())
    }
  }

  def resolveCallVar(name: String, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case impl: EntityImpl => findInImpl(name, impl, context)
        case valueType: EntityValue => findInEntity(name, valueType, context)
        case _ => Right(callable)
      }
    }
  }

  def resolveArray(position: Operation, array: ArrayValue, context: Context): Either[ExecError, Option[List[Value[_]]]] = {

    def resolve(posValue: Value[_]): Either[ExecError, Option[List[Value[_]]]] = {
      array.tbl match {
        case Some(array) => posValue match {
          case long: TLangLong => Right(Some(List(array(long.getElement.toInt).value)))
          case str: TLangString =>
            val callRes = array.find(elem => elem.attr.isDefined && elem.attr.get.equals(str.getElement))
            if (callRes.isDefined) Right(Some(List(callRes.get.value)))
            else Left(CallableNotFound(posValue.getElement.toString))
          case _ => Left(WrongType("Should be Int or String instead of " + posValue.getType))

        }
        case None => Left(CallableNotFound("position"))
      }

    }

    /*loopOverStatement(position.statements, context) match {
      case Left(error) => Left(error)
      case Right(res) => pickFirst(res) match {
        case Left(err) => Left(err)
        case Right(value) => resolve(value)
      }
    }*/
    ExecOperation.run(position, context) match {
      case Left(error) => Left(error)
      case Right(res) => pickFirst(res) match {
        case Left(err) => Left(err)
        case Right(value) => resolve(value)
      }
    }

  }

  def resolveArrayInCallable(call: CallArrayObject, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case impl: EntityImpl => findInImpl(call.name, impl, context) match {
          case Left(err) => Left(err)
          case Right(array) => resolveArrayInCallable(call, array, context)
        }
        case array: ArrayValue => resolveArray(call.position, array, context)
      }
    }
  }

  def findInImpl(name: String, impl: EntityImpl, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    if (impl.attrs.isDefined) findInAttrs(name, impl.attrs.get, context)
    else Left(CallableNotFound(name))
  }

  def findInEntity(name: String, entity: EntityValue, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    if (entity.attrs.isDefined) findInAttrs(name, entity.attrs.get, context)
    else Left(CallableNotFound(name))
  }

  def findInAttrs(name: String, attrs: List[ComplexAttribute], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    attrs.find(_.attr.getOrElse(false).equals(name)) match {
      case Some(value) => value.value match {
        case operation: Operation =>
          //          operation.content match {
          //            case Left(_) => Left(CallableNotFound(name))
          //            case Right(value) => value match {
          //              case caller: CallObject => loopOverStatement(caller.statements, context, 0, None)
          //              case _ =>
          //                Left(CallableNotFound(name))
          //            }
          //          }
          ExecOperation.run(operation, context)
        case _ => Right(Some(List(value.value)))
      }
      case None => Left(CallableNotFound(name))
    }
  }

  /* For now the returned functions are directly executed, a reference will be needed.
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
  }*/

  def pickFirst(callable: Option[List[Value[_]]]): Either[ExecError, Value[_]] = {
    if (callable.isDefined && callable.get.nonEmpty) Right(callable.get.head)
    else Left(CallableNotFound("It's empty"))
  }

}
