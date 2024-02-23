package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.resolver.{ResolverError, TypeError}
import tlang.core.Value

import scala.collection.mutable.ListBuffer

object CheckEntityType {

  def checkEntityType(entity: EntityValue, entityType: ModelSetEntity): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
//    if (entity.`type`.isEmpty || entity.`type`.get.getType != entityType.name) errors.addOne(TypeError(entity.context, entity.`type`.getOrElse("Undefined").toString, entityType.name))
//    if (entityType.params.isEmpty && entity.params.nonEmpty) errors.addOne(TypeError(entity.context, "params are empty in " + entityType.name, "params are not empty in entity"))
    if (entityType.attrs.nonEmpty) {
      entityType.attrs.foreach(_.foreach(attr => {
        if (entity.attrs.isDefined) {
          entity.attrs.get.find(entityAttr => entityAttr.`type`.isDefined
            && attr.attr.isDefined && entityAttr.`type`.get.getType.getType.toString == attr.attr.get) match {
            case Some(value) => checkType(errors, attr.attr.get, value.value)
            case None => errors.addOne(TypeError(attr.context, attr.attr.get + " not found", attr.attr.get))
          }
        }
      }))
    }

    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def checkType(errors: ListBuffer[ResolverError], valType: String, value: Value[_]): Unit = {
    CheckType.checkType(valType, value) match {
      case Left(errs) => errors.addAll(errs)
      case Right(_) =>
    }
  }

}
