package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.{ArrayType, ObjType, ValueType}
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.{ModelSetEntity, ModelSetValueType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{BuildModuleTree, Module, Resource}
import dev.tlang.tlang.resolver.ResolveContext.extractErrors
import tlang.core.Null
import tlang.internal.ContextContent

import scala.collection.mutable.ListBuffer

object FollowType {

  def followType(valueType: ValueType, module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    valueType match {
      case ArrayType(context, preType, name) => findInside(context, preType, name)(module, uses, scope, currentResource)
      case ObjType(context, preType, name) => findInside(context, preType, name)(module, uses, scope, currentResource)
      case _ =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def findInside(context: Null[ContextContent], preType: Option[String], name: String, addPreType: Option[String] = None)(module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (preType.isEmpty) {
      browseModel(name, currentResource) match {
        case Left(err) => errors.addAll(err)
        case Right(value) => value match {
          case Some(value) =>
            val newName = if (addPreType.isDefined) addPreType.get + "." + name else name
            addModelInScope(newName, value, List(), scope)
          case None => errors.addOne(ResourceNotFound(context, name))
        }
      }
    } else extractErrors(errors, findOutside(context, preType.get, name)(module, uses, scope))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def findOutside(context: Null[ContextContent], preType: String, name: String)(module: Module, uses: List[DomainUse], scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    uses.foreach(use => {
      if (use.parts.last == preType) {
        ResolveUtils.findResource(use, module) match {
          case None =>
            errors.addOne(ResourceNotFound(use.context, use.parts.mkString("/")))
          case Some(resource) =>
            findInside(context, None, name, Some(preType))(module, uses, scope, resource)
        }
      }
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def browseModel(name: String, resource: Resource): Either[List[ResolverError], Option[ModelSetEntity]] = {
    val errors = ListBuffer.empty[ResolverError]
    var elem: Option[ModelSetEntity] = None
    resource.ast.body.foreach {
      case ModelBlock(_, contents) => if (contents.isDefined) {
        ResolveUtils.findInModels(contents.get, name) match {
          case Some(model) => elem = Some(model)
          case None =>
        }
      }
      case _ =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(elem)
  }

  def addModelInScope(lastName: String, model: ModelSetValueType[_], previousNames: List[String], scope: Scope): Either[List[ResolverError], Option[ModelSetValueType[_]]] = {
    val name = if (previousNames.nonEmpty) BuildModuleTree.createPkg(previousNames.mkString("/"), lastName) else lastName
    scope.models.addOne(name, model)
    Right(Some(model))
  }


}
