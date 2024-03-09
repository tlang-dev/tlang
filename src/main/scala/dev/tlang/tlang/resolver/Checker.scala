package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.AssignVar
import dev.tlang.tlang.ast.helper.HelperBlock
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.value.{InterEntity, InterFunc, InterModel, InterResource, InterValue}
import dev.tlang.tlang.resolver.BuildLinkTree.{buildBody, buildHeader, combine, getClass}
import dev.tlang.tlang.tmpl.AstAnyTmplBlock
import tlang.internal.DomainBlock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Checker {

  def buildLinkTree(context: ResolverContext): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val currentPath = context.getResType
    context.resource.ast.body.foreach(body => combine(buildBody(context, body, currentPath), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHeader(domain: DomainModel): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    domain.header.foreach(_.uses.foreach(_.foreach(use => {

    })))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildBody(context: ResolverContext, domain: DomainBlock,  currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    domain match {
      case model: ModelBlock => combine(buildModelLink(context, model,  currentPath), errors)
      case helper: HelperBlock => combine(buildHelperLink(context, helper,  currentPath), errors)
      case tmpl: AstAnyTmplBlock => combine(buildTmplLink(context, tmpl,  currentPath), errors)
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildModelLink(context: ResolverContext, model: ModelBlock,  currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    model.content.foreach(_.foreach {
      case assignVar: AssignVar =>
        val name = assignVar.name
        val fullName = currentPath + "/" + name
//        links += (fullName -> InterEntity(ManualType(currentPath, name)))
      case setEntity: ModelSetEntity =>
        val name = setEntity.name.getSimpleType.toString
        val fullName = currentPath + "/" + name
//        links += (fullName -> InterModel(ManualType(currentPath, name)))
      case _ => println(getClass.getSimpleName + ": No other implementation")
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHelperLink(context: ResolverContext, helper: HelperBlock, currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    helper.funcs.foreach(_.foreach(func => {
      val name = func.name
      val fullName = currentPath + "/" + name
//      links += (fullName -> InterFunction(ManualType(currentPath, name)))
    }))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmplLink(context: ResolverContext, tmpl: AstAnyTmplBlock,currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def combine(res: Either[List[ResolverError], Unit], errors: ListBuffer[ResolverError]): Unit = {
    res match {
      case Left(value) => errors ++= value
      case Right(_) =>
    }
  }

}
