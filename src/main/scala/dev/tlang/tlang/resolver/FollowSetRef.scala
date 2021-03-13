package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.model.set.ModelSetRef
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable.ListBuffer

object FollowSetRef {

  def followSetRef(setRef: ModelSetRef, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    ResolveContext.browseBody(setRef.refs.head, currentResource) match {
      case Left(errs) => errors.addAll(errs)
      case Right(value) =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

}
