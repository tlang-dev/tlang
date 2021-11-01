package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.value.AssignVar
import dev.tlang.tlang.ast.helper.HelperBlock
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.interpreter.WrongType
import dev.tlang.tlang.loader.Resource
import dev.tlang.tlang.resolver.ResolverError

import scala.collection.mutable.ListBuffer

object CheckVariableType {

  def checkVariables(resource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    resource.ast.body.foreach {
      case helper: HelperBlock => checkInHelper(helper) match {
        case Left(value) => errors.addAll(value)
        case Right(_) =>
      }
      case model: ModelBlock => checkInModel(model) match {
        case Left(value) => errors.addAll(value)
        case Right(_) =>
      }
      case _ =>
    }
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkInHelper(helperBlock: HelperBlock): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkInModel(model: ModelBlock): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    model.content.foreach(_.foreach {
      case assign: AssignVar => manageType(assign)
      case _ =>
    })
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def manageType(assignVar: AssignVar): Either[List[ResolverError], Unit] = {
    FindOperationType.findOperationType(assignVar.value) match {
      case Left(error) => Left(List(error))
      case Right(value) =>
        if (assignVar.`type`.isDefined) {
          if (assignVar.`type`.get != value) Left(List(WrongType(assignVar.`type`.get.getType + " != " + value.getType, assignVar.context)))
        } else {
          assignVar.`type` = Some(value)
          Right(())
        }
    }
  }

}
