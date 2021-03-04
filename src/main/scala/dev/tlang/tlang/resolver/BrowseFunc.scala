package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable.ListBuffer

object BrowseFunc {

  def resolveFuncs(funcs: List[HelperFunc], module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    funcs.foreach(func => resolveFunc(func, module, uses, currentResource) match {
      case Left(errs) => errors.addAll(errs)
      case Right(_) =>
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveFunc(func: HelperFunc, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    BrowseHelperStatement.browseStatements(func.block.content, module, uses, func.scope, currentResource) match {
      case Left(err) => Left(err)
      case _ => Right(())
    }
  }

}
