package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.{ArrayType, ObjType, ValueType}
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.set._
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.context.{Context, ContextUtils}
import dev.tlang.tlang.resolver.{DoesNotExist, Element, ResolverError, ResourceNotFound}

object FollowCallToTheEnd {

  def followCallToTheEnd(call: CallObject, context: Context): Either[ResolverError, Option[Element[_]]] = {
    followCall(call, context)
  }

  def followCall(callObject: CallObject, context: Context, callIndex: Int = 0): Either[ResolverError, Option[Element[_]]] = {
    callObject.statements(callIndex) match {
      case array: CallArrayObject => followCallArray(array, callObject, context, callIndex)
      case callFunc: CallFuncObject => followCallFunc(callFunc, callObject, context, callIndex)
      case callRefFunc: CallRefFuncObject => followCallRefFunc(callRefFunc, callObject, context, callIndex)
      case variable: CallVarObject => followCallVar(variable, callObject, context, callIndex)
    }

  }

  def followCallVar(callVar: CallVarObject, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    ContextUtils.findModel(context, callVar.name) match {
          case Some(value) =>
            if (callIndex >= callObject.statements.length - 1) Right(Some(value))
            else followModel(value, callObject, context, callIndex + 1)
      case None => Left(ResourceNotFound(callVar.context, callVar.name))
    }
  }

  def followCallArray(callArray: CallArrayObject, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    Right(None)
  }

  def followCallRefFunc(callRefFunc: CallRefFuncObject, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    Right(None)
  }

  def followCallFunc(callFunc: CallFuncObject, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    ContextUtils.findFunc(context, callFunc.name.getOrElse("")) match {
      case Some(func) => followFuncReturnType(func.returns, callObject, context, callIndex)
      case None => Left(ResourceNotFound(callFunc.context, callFunc.name.getOrElse("")))
    }
  }

  def followFuncReturnType(params: Option[List[ValueType]], callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    params match {
      case Some(value) =>
        if (value.length == 1) followParamType(value.head, callObject, context, callIndex)
        else Right(None)
      case None => Right(None)
    }
  }

  def followModel(model: ModelSetValueType[_], callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    model match {
      case array: ModelSetArray => followSetArray(array, callObject, context, callIndex)
      case attr: ModelSetAttribute => followSetAttribute(attr, callObject, context, callIndex)
      case funcDef: ModelSetFuncDef => followSetFunc(funcDef, callObject, context, callIndex)
      case entity: ModelSetEntity => followSetEntity(entity, callObject, context, callIndex)
      case refFunc: ModelSetRef => followSetRef(refFunc, callObject, context, callIndex)
      case setType: ModelSetType => followSetType(setType, callObject, context, callIndex)
    }
  }

  /*def followType(value: Value[_], callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    value match {
      case entity: EntityValue =>
        if (callObject.statements.length - 1 == callIndex) Right(Some(entity))
        else followEntity(entity, callObject, context, callIndex + 1)
      case func: HelperFunc =>
      case tmplFunc: TmplFunc =>
      case _ => Right(Some(value))
    }
  }*/

  //  def followEntity(entity: EntityValue, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
  //
  //    callObject.statements(callIndex) match {
  //      case array: CallArrayObject => entity
  //      case callFunc: CallFuncObject => followCallFunc(callFunc, callObject, context, callIndex)
  //      case refFunc: CallRefFuncObject =>
  //      case variable: CallVarObject => followCallVar(variable, callObject, context, callIndex)
  //    }
  //  }

  def followParamType(paramType: ValueType, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    paramType match {
      case arrayType: ArrayType => Right(None)
      case funcType: HelperFuncType => Right(None)
      case objType: ObjType => ContextUtils.findModel(context, objType.name) match {
        case Some(value) => followNextCall(objType.getContext, Some(value), callObject, context, callIndex)
        case None => Left(ResourceNotFound(objType.context, objType.name))
      }
    }

  }

  def followSetArray(arrayType: ModelSetArray, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    Right(None)
  }

  def followSetAttribute(attr: ModelSetAttribute, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    Right(None)
  }

  def followSetFunc(funcDef: ModelSetFuncDef, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    Right(None)
  }

  def followSetEntity(entity: ModelSetEntity, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {

    def findInEntity(name: String, entity: ModelSetEntity): Either[ResolverError, Option[Element[_]]] = {
      findInAttrs(name, entity.params) match {
        case Some(value) => Right(value)
        case None => findInAttrs(name, entity.attrs) match {
          case Some(value) => Right(value)
          case None => Left(ResourceNotFound(entity.context, name))
        }
      }
    }

    def findInAttrs(name: String, attrs: Option[List[ModelSetAttribute]]): Option[Option[Element[_]]] = {
      if (attrs.isDefined) attrs.get.find(_.attr.getOrElse(false).equals(name)) match {
        case Some(value) => Some(Some(value.value))
        case None => None
      } else None
    }

    val callResp = callObject.statements(callIndex) match {
      case CallArrayObject(context, name, position) => Right(None)
      case callFunc: CallFuncObject =>
        findInEntity(callFunc.name.get, entity) match {
          case Left(err) => Left(err)
          case Right(optRefFunc) => optRefFunc match {
            case Some(refFunc) => followSetRef(refFunc.asInstanceOf[ModelSetRef], callObject, context, callIndex)
            case None => Left(ResourceNotFound(entity.getContext, callFunc.name.get))
          }
        }
      //        followCallFunc(callFunc, callObject, context, callIndex)
      //      case CallRefFuncObject(context, name, currying, func) => Right(None)
      case callVar: CallVarObject =>
        findInEntity(callVar.name, entity)
      case _ => Right(None)
    }

    callResp match {
      case Left(err) => Left(err)
      case Right(value) =>
        if (callIndex == callObject.statements.length - 1) Right(value)
        else followNextCall(entity.getContext, value, callObject, context, callIndex)
    }

  }

  def followSetRef(refFunc: ModelSetRef, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    val name = refFunc.refs.mkString("/")
    ContextUtils.findFunc(context, name) match {
      case Some(func) => followFuncReturnType(func.returns, callObject, context, callIndex)
      case None =>
        ContextUtils.findRefFunc(context, name) match {
          case Some(value) => Right(None)
          case None => ContextUtils.findVar(context, name) match {
            case Some(value) => Right(None)
            case None => Left(ResourceNotFound(refFunc.getContext, name))
          }
        }
    }
  }

  def followSetType(setType: ModelSetType, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    if (callIndex == callObject.statements.length - 1) Right(Some(setType))
    else Right(None)
  }

  def followNextCall(previousContext: Option[ContextContent], nextElement: Option[Element[_]], callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    if (callIndex >= callObject.statements.length - 1) Right(nextElement)
    else if (nextElement.isDefined) {
      nextElement.get match {
        case entity: EntityValue => followEntity(entity, callObject, context, callIndex + 1)
        case setEntity: ModelSetEntity => followSetEntity(setEntity, callObject, context, callIndex + 1)
        case _ => Left(DoesNotExist(nextElement.get.getContext, callObject.statements(callIndex - 1).toString))
      }
    } else Left(DoesNotExist(previousContext, callObject.statements(callIndex).toString))
  }

  def followEntity(entity: EntityValue, callObject: CallObject, context: Context, callIndex: Int): Either[ResolverError, Option[Element[_]]] = {
    Right(None)
  }

}
