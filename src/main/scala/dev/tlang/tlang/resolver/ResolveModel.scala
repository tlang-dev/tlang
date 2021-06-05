package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.value.AssignVar
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetRef, ModelSetValueType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{Module, Resource}
import dev.tlang.tlang.resolver.ResolveContext.extractErrors

import scala.collection.mutable.ListBuffer

object ResolveModel {

  def resolveModel(model: ModelBlock, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    model.content.foreach(_.foreach {
      case assign: AssignVar =>
        BrowseHelperStatement.browseAssignVar(assign, module, uses, assign.scope, currentResource) match {
          case Left(errs) => errors.addAll(errs)
          case Right(_) =>
        }
      case setEntity: ModelSetEntity => resolveSetEntity(setEntity, module, uses, currentResource)
      case _ =>
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveSetEntity(entity: ModelSetEntity, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (entity.ext.isDefined) extractErrors(errors, FollowType.followType(entity.ext.get, module, uses, entity.scope, currentResource))
    entity.attrs.foreach(_.foreach(attr => resolveModelType(attr, module, uses, currentResource, entity.scope)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveModelType(modelType: ModelSetAttribute, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    modelType.value match {
      case ref: ModelSetRef => FollowSetRef.followSetRef(ref, module, uses, currentResource)
      case _ => Right(())
    }
  }


}
