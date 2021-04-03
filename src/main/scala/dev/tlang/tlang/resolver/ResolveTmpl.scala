package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject}
import dev.tlang.tlang.ast.tmpl.call.{TmplCallArray, TmplCallFunc, TmplCallObj, TmplCallVar}
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import dev.tlang.tlang.ast.tmpl.loop.{TmplDoWhile, TmplFor, TmplWhile}
import dev.tlang.tlang.ast.tmpl.primitive.TmplPrimitiveValue
import dev.tlang.tlang.ast.tmpl.{TmplPkg, _}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.libraries.builtin.BuiltIntLibs
import dev.tlang.tlang.loader.{Module, Resource}

import scala.collection.mutable.ListBuffer

object ResolveTmpl {

  def resolveTmpl(block: TmplBlock, module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    checkRet(errors, resolvePkg(block.pkg, module, uses, currentResource, block.scope))
    checkRet(errors, resolveUses(block.uses, module, uses, currentResource, block.scope))
    block.content.foreach(_.foreach(content => {
      checkRet(errors, resolveContent(content, module, uses, currentResource, block.scope))
    }))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolvePkg(pkg: Option[TmplPkg], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    pkg.foreach(_.parts.foreach(part => checkRet(errors, resolveTmplId(part, module, uses, currentResource, scope))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveUses(tmplUses: Option[List[TmplUse]], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    tmplUses.foreach(_.foreach(use => {
      use.alias.foreach(alias => checkRet(errors, resolveTmplId(alias, module, uses, currentResource, scope)))
      use.parts.foreach(part => checkRet(errors, resolveTmplId(part, module, uses, currentResource, scope)))
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

  def resolveExprBlock(content: Option[TmplExprBlock], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    content.foreach(_.exprs.foreach(expr => checkRet(errors, resolveExpr(expr, module, uses, currentResource, scope))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveExpr(expr: TmplExpression, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    expr match {
      case tmplVar: TmplVar => checkRet(errors, resolveVar(tmplVar, module, uses, currentResource, scope))
      case value: TmplPrimitiveValue => checkRet(errors, resolvePrimitive(value, module, uses, currentResource, scope))
      case call: TmplCallObj => checkRet(errors, resolveCall(call, module, uses, currentResource, scope))
      case operation: TmplOperation => checkRet(errors, resolveOperation(operation, module, uses, currentResource, scope))
      case func: TmplFunc => checkRet(errors, resolveFunc(func, module, uses, currentResource, scope))
      case tmplIf: TmplIf => checkRet(errors, resolveIf(tmplIf, module, uses, currentResource, scope))
      case tmplFor: TmplFor => checkRet(errors, resolveFor(tmplFor, module, uses, currentResource, scope))
      case tmplWhile: TmplWhile => checkRet(errors, resolveWhile(tmplWhile, module, uses, currentResource, scope))
      case doWhile: TmplDoWhile => checkRet(errors, resolveDoWhile(doWhile, module, uses, currentResource, scope))
      case include: TmplInclude => checkRet(errors, resolveInclude(include, module, uses, currentResource, scope))
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveCall(call: TmplCallObj, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    call.calls.foreach {
      case array: TmplCallArray => {
        checkRet(errors, resolveOperation(array.elem, module, uses, currentResource, scope))
        checkRet(errors, resolveTmplId(array.name, module, uses, currentResource, scope))
      }
      case func: TmplCallFunc => {
        checkRet(errors, resolveTmplId(func.name, module, uses, currentResource, scope))
        func.currying.foreach(_.foreach(_.params.foreach(_.foreach(param => checkRet(errors, resolveSetAttribute(param, module, uses, currentResource, scope))))))
      }
      case callVar: TmplCallVar => checkRet(errors, resolveTmplId(callVar.name, module, uses, currentResource, scope))
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveSetAttribute(setAttribute: TmplSetAttribute, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    setAttribute.name.foreach(name => checkRet(errors, resolveTmplId(name, module, uses, currentResource, scope)))
    checkRet(errors, resolveOperation(setAttribute.value, module, uses, currentResource, scope))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveFor(tmplFor: TmplFor, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveWhile(tmplWhile: TmplWhile, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    checkRet(errors, resolveOperation(tmplWhile.cond, module, uses, currentResource, scope))
    checkRet(errors, resolveExprContent(tmplWhile.content, module, uses, currentResource, scope))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveDoWhile(doWhile: TmplDoWhile, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    checkRet(errors, resolveOperation(doWhile.cond, module, uses, currentResource, scope))
    checkRet(errors, resolveExprContent(doWhile.content, module, uses, currentResource, scope))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveIf(tmplIf: TmplIf, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    checkRet(errors, resolveOperation(tmplIf.cond, module, uses, currentResource, scope))
    checkRet(errors, resolveExprContent(tmplIf.content, module, uses, currentResource, scope))
    if (tmplIf.elseBlock.isDefined) tmplIf.elseBlock.get match {
      case Left(block) => checkRet(errors, resolveExprContent(block, module, uses, currentResource, scope))
      case Right(nextIf) => checkRet(errors, resolveIf(nextIf, module, uses, currentResource, scope))
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveOperation(operation: TmplOperation, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    operation.content match {
      case Left(subOp) => checkRet(errors, resolveOperation(subOp, module, uses, currentResource, scope))
      case Right(expr) => checkRet(errors, resolveExpr(expr, module, uses, currentResource, scope))
    }
    if (operation.next.isDefined) checkRet(errors, resolveOperation(operation.next.get._2, module, uses, currentResource, scope))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveVar(tmplVar: TmplVar, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    checkRet(errors, resolveAnnots(tmplVar.annots, module, uses, currentResource, scope))
    checkRet(errors, resolveProps(tmplVar.props, module, uses, currentResource, scope))
    checkRet(errors, resolveTmplId(tmplVar.name, module, uses, currentResource, scope))
    tmplVar.`type`.foreach(t => checkRet(errors, resolveType(t, module, uses, currentResource, scope)))
    tmplVar.value.foreach(t => checkRet(errors, resolveOperation(t, module, uses, currentResource, scope)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveFunc(func: TmplFunc, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    checkRet(errors, resolveAnnots(func.annots, module, uses, currentResource, scope))
    checkRet(errors, resolveProps(func.props, module, uses, currentResource, scope))
    checkRet(errors, resolveTmplId(func.name, module, uses, currentResource, scope))
    checkRet(errors, resolveCurrying(func.curries, module, uses, currentResource, scope))
    checkRet(errors, resolveExprBlock(func.content, module, uses, currentResource, scope))
    func.ret.foreach(_.foreach(ret => checkRet(errors, resolveType(ret, module, uses, currentResource, scope))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveCurrying(curries: Option[List[TmplFuncCurry]], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    curries.foreach(_.foreach(_.params.foreach(_.foreach(param => {
      checkRet(errors, resolveTmplId(param.name, module, uses, currentResource, scope))
      checkRet(errors, resolveType(param.`type`, module, uses, currentResource, scope))
    }))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveType(`type`: TmplType, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    checkRet(errors, resolveTmplId(`type`.name, module, uses, currentResource, scope))
    checkRet(errors, resolveGen(`type`.generic, module, uses, currentResource, scope))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveGen(generic: Option[TmplGeneric], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    generic.foreach(_.types.foreach(t => checkRet(errors, resolveType(t, module, uses, currentResource, scope))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveAnnots(annots: Option[List[TmplAnnotation]], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    annots.foreach(_.foreach(annot => {
      checkRet(errors, resolveTmplId(annot.name, module, uses, currentResource, scope))
      annot.values.foreach(_.foreach(param => {
        checkRet(errors, resolveTmplId(param.name, module, uses, currentResource, scope))
        checkRet(errors, resolvePrimitive(param.value, module, uses, currentResource, scope))
      }))
    }))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolvePrimitive(value: TmplPrimitiveValue, module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def resolveProps(props: Option[TmplProp], module: Module, uses: List[DomainUse], currentResource: Resource, scope: Scope): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    props.foreach(_.props.foreach(prop => checkRet(errors, resolveTmplId(prop, module, uses, currentResource, scope))))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
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
      case TmplInterpretedID(_, _, call, _) => resolveCallObj(call, module, uses, currentResource, scope)
      case TmplBlockID(_, block) => resolveTmpl(block, module, uses, currentResource)
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
          BrowseHelperStatement.browseCallFuncObjectParams(callFuncObject.currying, func, module, uses, scope, currentResource)
          Right(())
        } else FollowCallObject.followCallObject(call, module, uses, scope, currentResource)
      case _ => FollowCallObject.followCallObject(call, module, uses, scope, currentResource)
    }
    Right(())
  }

  def checkRet(errors: ListBuffer[ResolverError], ret: Either[List[ResolverError], Unit]): Unit = {
    ret match {
      case Left(err) => errors.addAll(err)
      case _ =>
    }
  }
}
