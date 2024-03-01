package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.value.AssignVar
import dev.tlang.tlang.ast.helper.{HelperBlock, HelperFunc}
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.loader.Resource
import dev.tlang.tlang.resolver.{NameAlreadyUsed, ResolverError}
import dev.tlang.tlang.tmpl.{AnyTmplInterpretedBlock, AstContext}
import tlang.internal.Context

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object CheckExistingElement {

  def checkExistingElement(resource: Resource): Either[List[ResolverError], Unit] = {
    val usedNames = mutable.Map.empty[String, AstContext]
    val errors = ListBuffer.empty[ResolverError]
    checkInHead(resource, usedNames) match {
      case Left(errs) => errors.addAll(errs)
      case Right(_) =>
    }
    checkInBody(resource, usedNames) match {
      case Left(errs) => errors.addAll(errs)
      case Right(_) =>
    }
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkInHead(resource: Resource, usedNames: mutable.Map[String, AstContext]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    resource.ast.header.foreach(_.uses.foreach(_.foreach(use => {
      val name = if (use.alias.isDefined) use.alias.get else use.parts.last
//      checkExisting(name, use, usedNames, errors)
    })))
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkInBody(resource: Resource, usedNames: mutable.Map[String, AstContext]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    resource.ast.body.foreach {
      case helper: HelperBlock => checkInHelper(helper, usedNames) match {
        case Left(value) => errors.addAll(value)
        case Right(_) =>
      }
      case model: ModelBlock => checkInModel(model, usedNames) match {
        case Left(value) => errors.addAll(value)
        case Right(_) =>
      }
      case tmpl: AnyTmplInterpretedBlock[_] => checkInTmpl(tmpl, usedNames) match {
        case Left(value) => errors.addAll(value)
        case Right(_) =>
      }
    }
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkInHelper(helperBlock: HelperBlock, usedNames: mutable.Map[String, AstContext]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    helperBlock.funcs.foreach(_.foreach(func => {
      checkFunc(func, usedNames) match {
        case Left(errs) => errors.addAll(errs)
        case Right(_) =>
      }
    }))
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkFunc(func: HelperFunc, usedNames: mutable.Map[String, AstContext]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val usedNamesInFunc = mutable.Map.empty[String, AstContext]
    checkExisting(func.name, func, usedNames, errors)
    usedNamesInFunc.addAll(usedNames)
    func.block.content.foreach(_.foreach {
      case assign: AssignVar => checkExisting(assign.name, assign, usedNamesInFunc, errors)
      case _ =>
    })
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkInModel(model: ModelBlock, usedNames: mutable.Map[String, AstContext]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    model.content.foreach(_.foreach {
      case assign: AssignVar => checkExisting(assign.name, assign, usedNames, errors)
//      case setEntity: ModelSetEntity => checkExisting(setEntity.name, setEntity, usedNames, errors)
    })
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkInTmpl(tmpl: AnyTmplInterpretedBlock[_], usedNames: mutable.Map[String, AstContext]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    //checkExisting(tmpl.name, tmpl, usedNames, errors)
    if (errors.isEmpty) Right(())
    else Left(errors.toList)
  }

  def checkExisting(name: String, node: AstContext, usedNames: mutable.Map[String, AstContext], errors: ListBuffer[ResolverError]): Unit = {
    if (usedNames.contains(name)) errors.addOne(NameAlreadyUsed(node.getContext, name))
    else usedNames.addOne((name, node))
  }

}
