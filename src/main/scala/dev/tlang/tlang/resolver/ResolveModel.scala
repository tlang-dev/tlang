package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.value.AssignVar
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.{ModelSetEntity, ModelSetRef, ModelSetValueType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable.ListBuffer

object ResolveModel {

  def resolveModel(model: ModelBlock, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    model.content.foreach(_.foreach {
      case assign: AssignVar =>
        assign.scope.foreach(scope => {
          BrowseHelperStatement.browseAssignVar(assign, module, uses, scope, currentResource) match {
            case Left(errs) => errors.addAll(errs)
            case Right(_) =>
          }
        })
      case setEntity: ModelSetEntity => resolveSetEntity(setEntity, module, uses, currentResource)
      case _ =>
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveSetEntity(entity: ModelSetEntity, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    entity.attrs.foreach(_.foreach(attr => resolveModelType(attr, module, uses, currentResource, entity.scope)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveModelType(modelType: ModelSetValueType[_], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    modelType match {
      case ref: ModelSetRef => FollowCallObject.followCallObject(ref, module, uses, scope, currentResource, None)
      case _ => Right(())
    }
  }



}
