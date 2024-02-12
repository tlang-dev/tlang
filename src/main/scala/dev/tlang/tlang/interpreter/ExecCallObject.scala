package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper.{HelperFunc, HelperStatement}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.ExecCallFunc.manageTmplParameters
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils, Scope}
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.lang.ast.LangBlockAsValue

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

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
              val tmplCopy = tmpl.deepCopy().asInstanceOf[AnyTmplInterpretedBlock[_]]
              val newContext = manageTmplParameters(func, tmplCopy, context)
              Right(Some(List(LangBlockAsValue(tmplCopy.getContext, tmplCopy, Context(newContext.scopes :+ tmplCopy.getScope)))))
            case None => Left(CallableNotFound(name, func.context))
          }
        }
      case variable: CallVarObject => findVar(callVar.name + "/" + variable.name, context)
      case refFunc: CallRefFuncObject => Right(Some(List(refFunc)))
    }
  }

  private def findInContext(statement: CallObjectType, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case CallArrayObject(contextContent, name, position) => ContextUtils.findVar(context, name) match {
        case Some(array) => resolveArray(position, array.asInstanceOf[ArrayValue], context)
        case None => Left(CallableNotFound(name, contextContent))
      }
      case caller: CallFuncObject => ExecCallFunc.run(caller, context)
      case refFunc: CallRefFuncObject => Right(Some(List(refFunc)))
      case CallVarObject(_, name) => findVar(name, context)
      case _ => Left(NotImplemented(context = None))
    }
  }

  private def findVar(name: String, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    ContextUtils.findVar(context, name) match {
      case Some(value) =>
        value match {
          case operation: Operation => ExecOperation.run(operation, context)
          case _ => Right(Some(List(value)))
        }
      case None => Left(CallableNotFound(name, None))
    }
  }

  private def findInCallable(statement: CallObjectType, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    statement match {
      case callArray: CallArrayObject => resolveArrayInCallable(callArray, callable, context)
      case caller: CallFuncObject => resolveFunc(caller, callable, context)
      case refFunc: CallRefFuncObject => Right(Some(List(refFunc)))
      case CallVarObject(_, name) => resolveCallVar(name, callable, context, statement)
      case _ => Left(NotImplemented(context = None))
    }
  }

  def resolveCallVar(name: String, callable: Option[List[Value[_]]], context: Context, caller: CallObjectType): Either[ExecError, Option[List[Value[_]]]] = {
    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case impl: EntityImpl => findInImpl(name, impl, context)
        case valueType: EntityValue => findInEntity(name, valueType, context, caller)
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
            else Left(CallableNotFound(posValue.getElement.toString, posValue.getContext))
          case _ => Left(WrongType("Should be Int or String instead of " + posValue.getType, posValue.getContext))

        }
        case None => Left(CallableNotFound("position", array.context))
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

  //  @tailrec
  private def resolveArrayInCallable(call: CallArrayObject, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case impl: EntityImpl => findInImpl(call.name, impl, context) match {
          case Left(err) => Left(err)
          case Right(array) => runIfOperation(resolveArrayInCallable(call, array, context), context)
        }
        case array: ArrayValue => resolveArray(call.position, array, context)
        case entityValue: EntityValue =>
          findInEntity(call.name, entityValue, context, call) match {
            case Left(_) => Left(CallableNotFound(call.name, entityValue.getContext))
            case Right(array) => runIfOperation(resolveArrayInCallable(call, array, context), context)
          }
      }
    }
  }

  def findInImpl(name: String, impl: EntityImpl, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    if (impl.attrs.isDefined) findInAttrs(name, impl.attrs.get, context)
    else Left(CallableNotFound(name, impl.context))
  }

  def findInEntity(name: String, entity: EntityValue, context: Context, caller: CallObjectType): Either[ExecError, Option[List[Value[_]]]] = {
    if (entity.attrs.isDefined) findInAttrs(name, entity.attrs.get, context) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case Some(value) => Right(Some(value))
        case None =>
          if (name == "type")
            Right(Some(List(new TLangString(None, entity.getType))))
          else findModelInEntity(name, entity, context, caller)
      }
    }
    else if (name == "type")
      Right(Some(List(new TLangString(None, entity.getType))))
    else findModelInEntity(name, entity, context, caller)
  }

  def findInAttrs(name: String, attrs: List[ComplexAttribute], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    attrs.find(_.attr.getOrElse(false).equals(name)) match {
      case Some(value) => value.value match {
        case operation: Operation => ExecOperation.run(operation, context)
        case _ => Right(Some(List(value.value)))
      }
      case None => Right(None)
    }
  }

  def findModelInEntity(name: String, entity: EntityValue, context: Context, caller: CallObjectType): Either[ExecError, Option[List[Value[_]]]] = {
    entity.`type` match {
      case Some(entityType) => findModelFromType(name, entityType, entity.scope, context, caller)
      case None => Left(CallableNotFound(name, entity.getContext))
    }
  }

  def findModelFromType(name: String, modelType: ValueType, currentScope: Scope, context: Context, caller: CallObjectType): Either[ExecError, Option[List[Value[_]]]] = {
    val typeName = modelType.getContextType
    currentScope.models.get(typeName) match {
      case Some(value) =>
        val model = value.asInstanceOf[ModelSetEntity]
        findInModel(name, model, Context(model.scope :: context.scopes), caller) match {
          case Left(error) => Left(error)
          case Right(res) => res match {
            case Some(_) => Right(res)
            case None =>
              if (model.ext.isDefined) findModelFromType(name, model.ext.get, model.scope, context, caller)
              else Left(CallableNotFound(name, modelType.getContext))
          }
        }
      case None => Left(CallableNotFound(name, modelType.getContext))
    }
  }

  def findInModel(name: String, model: ModelSetEntity, context: Context, caller: CallObjectType): Either[ExecError, Option[List[Value[_]]]] = {
    if (model.attrs.isDefined) findInSetAttrs(name, model.attrs.get, context, model.getContext, caller)
    else Right(None)
  }

  def findInSetAttrs(name: String, attrs: List[ModelSetAttribute], context: Context, contextContent: Option[ContextContent], caller: CallObjectType): Either[ExecError, Option[List[Value[_]]]] = {
    attrs.find(_.attr.getOrElse(false).equals(name)) match {
      case Some(value) => value.value match {
        case ref: ModelSetRef =>
          caller match {
            case _: CallFuncObject => Right(Some(List(ExecUtils.modelRefToCallRefFunc(ref))))
            case _: CallVarObject => ExecModelSetRef.run(ref, Context(List(ref.scope)))
          }

        //case funcRef: ModelSetFuncDef => Right(Some(List(funcRef)))
        case _ => Left(WrongType("Should be a ref", contextContent))
      }
      case None => Right(None)
    }
  }

  // For now the returned functions are directly executed, a reference will be needed.
  def resolveFunc(caller: CallFuncObject, callable: Option[List[Value[_]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {

    def execFunc(func: HelperFunc): Either[ExecError, Option[List[Value[_]]]] = {
      val newName = "_call_" + caller.name.getOrElse("func")
      val newCaller = CallFuncObject(func.context, Some(newName), caller.currying)
      execFuncWithCaller(newCaller, func, context)
    }

    pickFirst(callable) match {
      case Left(error) => Left(error)
      case Right(value) => value match {
        case func: HelperFunc => execFunc(func)
        case entity: EntityValue => execFuncInEntity(caller, entity, Context(entity.scope :: context.scopes))
        case ref: CallRefFuncObject =>
          val newName = "_call_" + ref.name.get
          val newCaller = CallFuncObject(ref.context, Some(newName), caller.currying)
          ref.func.get match {
            case Left(helperFunc) =>
              execFuncWithCaller(newCaller, helperFunc, context)
            case Right(tmplBlock) => Left(NotImplemented("The execution of TmplBloc is not yet implemented in a ref func in an array or entity", ref.context))
          }

        case _ => Left(NotImplemented(value.getType, value.getContext))
      }
    }
  }

  private def execFuncWithCaller(newCaller: CallFuncObject, func: HelperFunc, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val newScope = Scope(functions = mutable.Map(newCaller.name.get -> func))
    val newContext = Context(context.scopes :+ newScope)
    ExecCallFunc.run(newCaller, newContext)
  }

  private def execRefFuncWithCaller(newCaller: CallFuncObject, refFunc: CallRefFuncObject, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val newScope = Scope(refFunctions = mutable.Map(newCaller.name.get -> refFunc))
    val newContext = Context(context.scopes :+ newScope)
    ExecCallFunc.run(newCaller, newContext)
  }

  private def execFuncInEntity(caller: CallFuncObject, entity: EntityValue, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    findInEntity(caller.name.get, entity, Context(List(entity.scope)), caller) match {
      case Left(error) => Left(error)
      case Right(value) =>
        if (value.isEmpty || value.get.size != 1) Left(WrongValueReturned("Only one value was expected to be returned from entity attribute (" + caller.name + ")", caller.context))
        else {
          value.get.head match {
            case refFunc: CallRefFuncObject => execRefFuncWithCaller(caller, refFunc, context)
            case _ => Left(NotImplemented(value.get.head.getType, value.get.head.getContext))
          }
        }
    }
  }

  private def pickFirst(callable: Option[List[Value[_]]]): Either[ExecError, Value[_]] = {
    if (callable.isDefined && callable.get.nonEmpty) Right(callable.get.head)
    else Left(CallableNotFound("It's empty", None))
  }

  private def runIfOperation(result: Either[ExecError, Option[List[Value[_]]]], context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    var error: Option[ExecError] = None
    val values = ListBuffer.empty[Value[_]]
    result match {
      case Left(err) => error = Some(err)
      case Right(value) => value.map(instr => instr.map {
        case operation: Operation => ExecOperation.run(operation, context) match {
          case Left(err) => error = Some(err)
          case Right(value) => if (value.isDefined) values.addAll(value.get)
        }
        case _ => values.addOne(_)
      })
    }
    if (error.isDefined) Left(error.get)
    else if (values.isEmpty) Right(None)
    else Right(Some(values.toList))
  }

}
