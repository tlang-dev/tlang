package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable.ListBuffer

object FollowType {

  def followType(name:String,  module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    ResolveContext.browseBody(name, currentResource) match {
      case Left(err) => errors.addAll(err)
      case Right(value) => value match {
        case Some(value) =>
          ResolveContext.addValueInScope(name, value, List(), scope)
        case None => findOutside(name, module, uses, scope)
      }
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def findOutside(name: String,  module: Module, uses: List[DomainUse], scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    uses.foreach(use => {
      if (use.parts.last == name) {
        ResolveUtils.findResource(use, module) match {
          case None => errors.addOne(ResourceNotFound(use.context, use.parts.mkString("/")))
          case Some(resource) => followType(name, module, uses, scope, resource)
        }
      }
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

}
