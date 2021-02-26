package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject}
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call.TmplCallObj
import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.ast.tmpl.loop.{TmplDoWhile, TmplFor, TmplWhile}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.libraries.builtin.BuiltIntLibs
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable.ListBuffer

object ResolveTmpl {

  def resolveTmpl(block: TmplBlock, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (block.pkg.isDefined) {
      var i = 0
      while (i < block.pkg.get.parts.length) {
        resolveTmplId(block.pkg.get.parts(i), module, uses, currentResource, block.scope) match {
          case Left(err) => errors.addAll(err)
          case _ =>
        }
        i += 1
      }
    }
    block.content.foreach(_.foreach(content => {
      resolveContent(content, module, uses, currentResource, block.scope) match {
        case Left(error) => errors.addAll(error)
        case _ =>
      }
    }))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveContent(content: TmplContent, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    content match {
      case func: TmplFunc => resolveFunc(func, module, uses, currentResource, scope)
      case impl: TmplImpl => resolveImpl(impl, module, uses, currentResource, scope)
      case expr: TmplExprContent => resolveExprContent(expr, module, uses, currentResource, scope)
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveExprContent(expr: TmplExprContent, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    expr match {
      case exprs: TmplExprBlock => exprs.exprs.foreach(expression => {
        resolveExpr(expression, module, uses, currentResource, scope) match {
          case Left(error) => errors.addAll(error)
          case _ =>
        }
      })
      case expression: TmplExpression => resolveExpr(expression, module, uses, currentResource, scope) match {
        case Left(error) => errors.addAll(error)
        case _ =>
      }
      case _ =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveExpr(expr: TmplExpression, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    expr match {
      case tmplVar: TmplVar =>
      case call: TmplCallObj =>
      case condition: TmplConditionBlock =>
      case func: TmplFunc =>
      case tmplIf: TmplIf =>
      case tmplFor: TmplFor =>
      case tmplWhile: TmplWhile =>
      case doWhile: TmplDoWhile =>
      case include: TmplInclude => resolveInclude(include, module, uses, currentResource, scope) match {
        case Left(error) => errors.addAll(error)
        case _ =>
      }
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveFunc(func: TmplFunc, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    Right(())
  }

  def resolveImpl(impl: TmplImpl, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (impl.content.isDefined) {
      impl.content.get.foreach(content => {
        resolveContent(content, module, uses, currentResource, scope) match {
          case Left(error) => errors.addAll(error)
          case _ =>
        }
      })
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveInclude(include: TmplInclude, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    include.calls.foreach(call => {
      resolveCallObj(call, module, uses, currentResource, scope)
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveTmplId(tmplID: TmplID, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    tmplID match {
      case TmplInterpretedID(_, call, _) => resolveCallObj(call, module, uses, currentResource, scope)
      case TmplBlockID(block) => resolveTmpl(block, module, uses, currentResource)
      case _ =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveCallObj(call: CallObject, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    call.statements.head match {
      case callFuncObject: CallFuncObject =>
        if (callFuncObject.name.isDefined && BuiltIntLibs.buildIntLibs.contains(callFuncObject.name.get)) {
          val func = BuiltIntLibs.buildIntLibs(callFuncObject.name.get)
          scope.functions.addOne(func.name, func)
          ResolveStatement.resolveCallFuncObjectParams(callFuncObject.currying, func, module, uses, scope, currentResource)
          Right(())
        } else ResolveContext.resolveCallObject(call, module, uses, scope, currentResource)
      case _ => ResolveContext.resolveCallObject(call, module, uses, scope, currentResource)
    }
    Right(())
  }
}
