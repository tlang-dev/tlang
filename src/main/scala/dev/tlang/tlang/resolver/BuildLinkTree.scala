package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{AssignVar, ComplexValueType, MultiValue, PrimitiveValue}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.value._
import dev.tlang.tlang.tmpl.AstAnyTmplBlock
import tlang.core.Value
import tlang.internal.DomainBlock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object BuildLinkTree {

  def buildLinkTree(context: ResolverContext): Either[List[ResolverError], Map[String, InterValue]] = {
    val errors = ListBuffer.empty[ResolverError]
    val values = mutable.Map[String, InterValue]()
    val currentPath = context.getResType
    values += (context.getResType -> InterResource(ManualType(context.getFullPkg, context.getName)))
    combine(buildHeader(context.resource.ast), errors)
    context.resource.ast.body.foreach(body => combine(buildBody(context, body, values, currentPath), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(values.toMap)
  }

  private def buildHeader(domain: DomainModel): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    domain.header.foreach(_.uses.foreach(_.foreach(use => {

    })))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildBody(context: ResolverContext, domain: DomainBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    domain match {
      case model: ModelBlock => combine(buildModelLink(context, model, links, currentPath), errors)
      case helper: HelperBlock => combine(buildHelperLink(context, helper, links, currentPath), errors)
      case tmpl: AstAnyTmplBlock => combine(buildTmplLink(context, tmpl, links, currentPath), errors)
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildModelLink(context: ResolverContext, model: ModelBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    model.content.foreach(_.foreach {
      case assignVar: AssignVar =>
        val name = assignVar.name
        val fullName = currentPath + "/" + name
        links += (fullName -> InterEntity(ManualType(currentPath, name)))
      case setEntity: ModelSetEntity =>
        val name = setEntity.name.getSimpleType.toString
        val fullName = currentPath + "/" + name
        links += (fullName -> InterModel(ManualType(currentPath, name)))
      case _ => println(getClass.getSimpleName + ": No other implementation")
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHelperLink(context: ResolverContext, helper: HelperBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    helper.funcs.foreach(_.foreach(func => {
      val name = func.name
      val fullName = currentPath + "/" + name
      links += (fullName -> InterFunction(ManualType(currentPath, name)))
      combine(buildHelperFunc(context, func, links, currentPath), errors)
    }))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHelperFunc(context: ResolverContext, func: HelperFunc, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val newPath = currentPath + "/" + func.name
    combine(buildContent(context, func.block, links, newPath), errors)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildContent(context: ResolverContext, content: HelperContent, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    content.content.foreach(_.foreach(stmt => combine(buildStatement(context, stmt, links, currentPath), errors)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildStatement(context: ResolverContext, statement: HelperStatement, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    statement match {
      case stmt: HelperIf => combine(buildIf(context, stmt, links, currentPath), errors)
      case stmt: CallObject => combine(buildCallObject(context, stmt, links, currentPath), errors)
      case stmt: HelperFor =>
      //      case stmt: HelperFunc => ExecFunc.run(stmt, Context(List(stmt.scope)))
      case stmt: Operation => combine(buildOperation(context, stmt, links, currentPath), errors)
      //      case stmt: HelperInternalFunc =>
      //        val newContext = Context(context.scopes :+ stmt.scope)
      //        ExecInternalFunc.run(stmt, newContext)
      case stmt: AssignVar =>
      case stmt: PrimitiveValue[_] =>
      case stmt: MultiValue =>
      case stmt: ComplexValueType[_] =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildOperation(context: ResolverContext, operation: Operation, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    operation.content match {
      case Left(value) => combine(buildOperation(context, value, links, currentPath), errors)
      case Right(value) => combine(buildComplexValue(context, value, links, currentPath), errors)
    }
    operation.next.foreach(op => combine(buildOperation(context, op._2, links, currentPath), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildCallObject(context: ResolverContext, callObject: CallObject, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val callPath = currentPath + "/" + callObject.statements.map(_.getName).mkString("/")
    callObject.path = Some(currentPath)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildComplexValue(context: ResolverContext, value: ComplexValueStatement[_], links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    value match {
      case obj: CallObject => combine(buildCallObject(context, obj, links, currentPath), errors)
      case values: MultiValue =>
      case value: PrimitiveValue[_] =>
      case value: Value =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildIf(context: ResolverContext, helperIf: HelperIf, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    helperIf.ifTrue.foreach(content => combine(buildContent(context, content, links, currentPath), errors))
    helperIf.ifFalse.foreach(content => combine(buildContent(context, content, links, currentPath), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmplLink(context: ResolverContext, tmpl: AstAnyTmplBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmplLangLink(context: ResolverContext, tmpl: AstAnyTmplBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmplDocLink(context: ResolverContext, tmpl: AstAnyTmplBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmplDataLink(context: ResolverContext, tmpl: AstAnyTmplBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmplStyleLink(context: ResolverContext, tmpl: AstAnyTmplBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmplCmdLink(context: ResolverContext, tmpl: AstAnyTmplBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
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
