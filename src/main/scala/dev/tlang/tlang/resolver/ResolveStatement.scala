package dev.tlang.tlang.resolver

import dev.tlang.tlang.loader
import dev.tlang.tlang.loader.Resource
import io.sorne.tlang.ast.DomainUse
import io.sorne.tlang.ast.common.call.{CallFuncObject, CallObject, SimpleValueStatement}
import io.sorne.tlang.ast.common.condition.{Condition, ConditionBlock}
import io.sorne.tlang.ast.common.value.{ArrayValue, AssignVar, EntityValue, MultiValue}
import io.sorne.tlang.ast.helper.{HelperFor, HelperIf, HelperStatement}
import io.sorne.tlang.interpreter.context.Scope
import io.sorne.tlang.loader.{Module, Resource}

object ResolveStatement {

  def resolveStatements(statements: Option[List[HelperStatement]], module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    if (statements.nonEmpty) {
      var error: Option[ResolverError] = None
      var i = 0
      while (error.isEmpty && i < statements.get.size) {
        val statement = statements.get(i)
        ResolveStatement.resolveStatement(statement, module, uses, scope, currentResource) match {
          case Left(err) => error = Some(err)
          case _ =>
        }
        i += 1
      }
      if (error.isDefined) Left(error.get)
      else Right(())
    } else Right(())
  }

  def resolveStatement(statement: HelperStatement, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    statement match {
      case call: CallObject => ResolveContext.resolveCallObject(call, module, uses, scope, currentResource)
      case assignVar: AssignVar => resolveAssignVar(assignVar, module, uses, scope, currentResource)
      case helperIf: HelperIf => resolveIf(helperIf, module, uses, scope, currentResource)
      case helperFor: HelperFor => resolveFor(helperFor, module, uses, scope, currentResource)
      case conditionBlock: ConditionBlock => resolveConditionBlock(conditionBlock, module, uses, scope, currentResource)
      case multiValue: MultiValue => resolveMultiVal(multiValue, module, uses, scope, currentResource)
      case entity: EntityValue => resolveEntity(entity, module, uses, scope, currentResource)
      case array: ArrayValue => resolveArray(array, module, uses, scope, currentResource)
      case _ => Right(())
    }
  }

  def resolveAssignVar(assignVar: AssignVar, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    resolveStatement(assignVar.value, module, uses, scope, currentResource)
  }

  def resolveIf(helperIf: HelperIf, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    resolveStatement(helperIf.condition, module, uses, scope, currentResource)
    helperIf.ifTrue.foreach(block => resolveStatements(block.content, module, uses, scope, currentResource))
    helperIf.ifFalse.foreach(block => resolveStatements(block.content, module, uses, scope, currentResource))
    Right(())
  }

  def resolveFor(helperFor: HelperFor, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    helperFor.start.foreach(stmt => resolveStatement(stmt, module, uses, scope, currentResource))
    resolveStatement(helperFor.array, module, uses, scope, currentResource)
    resolveStatements(helperFor.body.content, module, uses, scope, currentResource)
    Right(())
  }

  def resolveConditionBlock(conditionBlock: ConditionBlock, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    conditionBlock.content match {
      case Left(block) => resolveStatement(block, module, uses, scope, currentResource)
      case Right(cond) => resolveCondition(cond, module, uses, scope, currentResource)
    }
    conditionBlock.nextBlock.foreach(block => resolveConditionBlock(block, module, uses, scope, currentResource))
    Right(())
  }

  def resolveCondition(condition: Condition, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    resolveStatement(condition.statement1, module, uses, scope, currentResource)
    condition.statement2.foreach(stmt => resolveStatement(stmt, module, uses, scope, currentResource))
    condition.nextBlock.foreach(block => resolveConditionBlock(block, module, uses, scope, currentResource))
    Right(())
  }

  def resolveMultiVal(multiValue: MultiValue, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    multiValue.values.foreach {
      case statement: SimpleValueStatement[_] => resolveStatement(statement, module, uses, scope, currentResource)
      case _ =>
    }
    Right(())
  }

  def resolveEntity(entity: EntityValue, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    entity.params.foreach(_.foreach(param => resolveStatement(param.value, module, uses, scope, currentResource)))
    entity.attrs.foreach(_.foreach(attr => resolveStatement(attr.value, module, uses, scope, currentResource)))
    Right(())
  }

  def resolveArray(array: ArrayValue, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    array.tbl.foreach(_.foreach(elem => resolveStatement(elem.value, module, uses, scope, currentResource)))
    Right(())
  }

  def resolveCallFuncObjectParams(call: CallFuncObject, module: loader.Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[ResolverError, Unit] = {
    call.currying.foreach(_.foreach(curry => curry.params.foreach(_.foreach(param => resolveStatement(param.value, module, uses, scope, currentResource)))))
    Right(())
  }

}
