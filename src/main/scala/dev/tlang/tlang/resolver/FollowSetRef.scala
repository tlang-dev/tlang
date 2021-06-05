package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallObject, CallObjectType, CallRefFuncObject, CallVarObject}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetRef, ModelSetRefCurrying}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{Module, Resource}
import dev.tlang.tlang.resolver.ResolveContext.extractErrors

import scala.collection.mutable.ListBuffer

object FollowSetRef {

  def followSetRef(setRef: ModelSetRef, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val callParts = ListBuffer.empty[CallObjectType]
    if (setRef.refs.size > 1) {
      callParts.addOne(CallVarObject(None, setRef.refs.head))
    }
    var callFunc: Option[CallRefFuncObject] = None
    if (setRef.currying.isDefined) {
      extractErrors(errors, followRefCurrying(setRef.currying.get, module, uses, setRef.scope, currentResource))
      callFunc = Some(CallRefFuncObject(None, Some(setRef.refs.last), None))
      callParts.addOne(callFunc.get)
    }
    else callParts.addOne(CallVarObject(None, setRef.refs.last))
    val caller = CallObject(None, callParts.toList)
    FollowCallObject.followCallObject(caller, module, uses, setRef.scope, currentResource)
    if (callFunc.isDefined) setRef.func = callFunc.get.func
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def followRefCurrying(currying: List[ModelSetRefCurrying], module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    currying.foreach(_.values.foreach {
      case op: Operation => BrowseHelperStatement.browseOperation(op, module, uses, scope, currentResource)
      case entity: EntityValue => BrowseNewEntity.browseEntity(entity, module, uses, currentResource)
      case setRef: ModelSetRef => followSetRef(setRef, module, uses, currentResource)
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

}
