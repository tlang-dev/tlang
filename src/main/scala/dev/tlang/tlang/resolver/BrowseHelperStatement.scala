package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallFuncParam, CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, AssignVar, EntityValue, MultiValue}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.helper.{HelperFor, HelperFunc, HelperIf, HelperStatement}
import dev.tlang.tlang.ast.tmpl.TmplBlockAsValue
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.interpreter.{ExecCallFunc, Value}
import dev.tlang.tlang.loader
import dev.tlang.tlang.loader.Resource
import dev.tlang.tlang.resolver.ResolveContext.extractErrors

import scala.collection.mutable.ListBuffer

object BrowseHelperStatement {

  def browseStatements(statements: Option[List[HelperStatement]], module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    if (statements.nonEmpty) {
      val errors = ListBuffer.empty[ResolverError]
      statements.get.foreach(statement => extractErrors(errors, browseStatement(statement, module, uses, scope, currentResource)))
      if (errors.nonEmpty) Left(errors.toList)
      else Right(())
    } else Right(())
  }

  def browseStatement(statement: HelperStatement, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource, newName: Option[String] = None): Either[List[ResolverError], Unit] = {
    statement match {
      case call: CallObject => FollowCallObject.followCallObject(call, module, uses, scope, currentResource, newName)
      case assignVar: AssignVar => browseAssignVar(assignVar, module, uses, scope, currentResource)
      case helperIf: HelperIf => browseIf(helperIf, module, uses, scope, currentResource)
      case helperFor: HelperFor => browseFor(helperFor, module, uses, scope, currentResource)
      case operation: Operation => browseOperation(operation, module, uses, scope, currentResource)
      case multiValue: MultiValue => browseMultiVal(multiValue, module, uses, scope, currentResource)
      case entity: EntityValue => BrowseNewEntity.browseEntity(entity, module, uses, currentResource)
      case array: ArrayValue => browseArray(array, module, uses, scope, currentResource)
      case _ => Right(())
    }
  }

  def browseAssignVar(assignVar: AssignVar, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    assignVar.value.content match {
      case Left(_) => browseStatement(assignVar.value, module, uses, scope, currentResource)
      case Right(value) => value match {
        case entityValue: EntityValue => entityValue.`type` = assignVar.`type`
          browseStatement(assignVar.value, module, uses, scope, currentResource)
        case _ => browseStatement(assignVar.value, module, uses, scope, currentResource)
      }
    }
  }

  def cleanType(valType: Option[ObjType]): Option[ValueType] = {
    if (valType.isDefined && valType.get.preType.isDefined)
      Some(ObjType(valType.get.context, None, valType.get.name))
    else valType
  }

  def browseIf(helperIf: HelperIf, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    extractErrors(errors, browseStatement(helperIf.condition, module, uses, scope, currentResource))
    helperIf.ifTrue.foreach(block => extractErrors(errors, browseStatements(block.content, module, uses, scope, currentResource)))
    helperIf.ifFalse.foreach(block => extractErrors(errors, browseStatements(block.content, module, uses, scope, currentResource)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def browseFor(helperFor: HelperFor, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    helperFor.start.foreach(stmt => extractErrors(errors, browseStatement(stmt, module, uses, scope, currentResource)))
    extractErrors(errors, browseStatement(helperFor.array, module, uses, scope, currentResource))
    extractErrors(errors, browseStatements(helperFor.body.content, module, uses, scope, currentResource))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def browseOperation(operation: Operation, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    operation.content match {
      case Left(op) => extractErrors(errors, browseOperation(op, module, uses, scope, currentResource))
      case Right(value) => extractErrors(errors, browseStatement(value, module, uses, scope, currentResource))
    }
    operation.next.foreach(block => extractErrors(errors, browseOperation(block._2, module, uses, scope, currentResource)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def browseMultiVal(multiValue: MultiValue, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    multiValue.values.foreach {
      case statement: ComplexValueStatement[_] => extractErrors(errors, browseStatement(statement, module, uses, scope, currentResource))
      case _ =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def browseArray(array: ArrayValue, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    array.tbl.foreach(_.foreach(elem => extractErrors(errors, browseStatement(elem.value, module, uses, scope, currentResource))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def browseCallFuncObjectParams(currying: Option[List[CallFuncParam]], called: Value[_], module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    currying.foreach(_.zipWithIndex.foreach(curry => curry._1.params.foreach(_.zipWithIndex.foreach(param => {
      val paramName = called match {
        case func: HelperFunc => ExecCallFunc.findParamName(curry._2, param._2, func)
        case tmpl: TmplBlockAsValue => ExecCallFunc.findTmplParamName(param._2, tmpl.block)
      }
      extractErrors(errors, browseStatement(param._1.value, module, uses, scope, currentResource, Some(paramName)))
    }))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

}
