package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityImpl, EntityValue}
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader
import dev.tlang.tlang.loader.Resource

import scala.collection.mutable.ListBuffer

object BrowseNewEntity {

  def browseEntity(entity: EntityValue, module: loader.Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    browseAttrs(entity.attrs, None, module, uses, entity.scope, currentResource)
  }

  def browseAttrs(attrs: Option[List[ComplexAttribute]], model: Option[ModelSetEntity], module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    attrs.foreach(_.foreach(attr => browseAttr(attr, model, module, uses, scope, currentResource)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def browseAttr(attr: ComplexAttribute, model: Option[ModelSetEntity], module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    attr.value.content match {
      case Left(op) => BrowseHelperStatement.browseStatement(op, module, uses, scope, currentResource)
      case Right(value) => value match {
        case impl: EntityImpl => browseImpl(impl, model, module, uses, scope, currentResource)
        case stmt: HelperStatement => BrowseHelperStatement.browseStatement(stmt, module, uses, scope, currentResource)
      }
    }
  }

  def browseImpl(impl: EntityImpl, model: Option[ModelSetEntity], module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    impl.model = model
    browseAttrs(impl.attrs, model, module, uses, scope, currentResource)
  }
}
