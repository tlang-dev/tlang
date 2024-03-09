package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainModel
import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.value._
import dev.tlang.tlang.loader.Resource
import dev.tlang.tlang.tmpl.AstAnyTmplBlock
import tlang.core.{Type, Value}
import tlang.internal.DomainBlock

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object BuildLinkTree {

  def buildLinkTree(context: ResolverContext): Either[List[ResolverError], Map[String, InterValue]] = {
    val errors = ListBuffer.empty[ResolverError]
    val values = mutable.Map[String, InterValue]()
    val currentPath = context.getResType
    values += (context.getResType -> InterResource(ManualType(context.getFullPkg, context.getName)))
    combine(buildHeader(context, context.resource.ast, values), errors)
    context.resource.ast.body.foreach(body => combine(buildBody(context, body, values, currentPath), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(values.toMap)
  }

  private def buildHeader(context: ResolverContext, domain: DomainModel, links: mutable.Map[String, InterValue]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    domain.header.foreach(_.uses.foreach(_.foreach(use => {
      val name = use.alias.getOrElse(use.parts.last)
      val pkgResource = context.getFullPkg + "/" + use.parts.head
      context.module.resources.get(pkgResource) match {
        case Some(value) => linkInternalResources(value, links, context.getResType)
        case None => //It's probably an external module or an internal class, let's see later
      }
    })))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def linkInternalResources(resource: Resource, links: mutable.Map[String, InterValue], preLink: String): Unit = {
    resource.ast.header.map(_.exposes.map(_.map(expose => {
      val name = expose.name
      resource.ast.body.foreach {
        case model: ModelBlock => model.content.foreach(_.foreach {
          case assignVar: AssignVar => if (assignVar.name == name) links.addOne((preLink + "/" + name) -> InterStaticVar(assignVar.getType))
          case model: ModelSetEntity => if (model.name.getSimpleType.toString == name) links.addOne((preLink + "/" + name) -> InterModel(model.name))
        })
        case helper: HelperBlock => helper.funcs.foreach(_.foreach(func => if (func.name == name) links.addOne((preLink + "/" + name) -> InterFunc(func.getType))))
        case tmpl: AstAnyTmplBlock => if (tmpl.getName == name) links.addOne((preLink + "/" + name) -> InterFunc(tmpl.getType))
      }
    })))
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
      case assignVar: AssignVar => combine(buildAssignVar(context, assignVar, links, currentPath), errors)
      case setEntity: ModelSetEntity =>
        val name = setEntity.name.getSimpleType.toString
        val fullName = currentPath + "/" + name
        links += (fullName -> InterModel(ManualType(currentPath, name)))
      case _ => println(getClass.getSimpleName + ": No other implementation")
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildAssignVar(context: ResolverContext, assignVar: AssignVar, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    implicit val `type`: Option[Type] = Some(ManualType(currentPath, assignVar.name))
    combine(buildOperation(context, assignVar.value, links, currentPath), errors)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHelperLink(context: ResolverContext, helper: HelperBlock, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    helper.funcs.foreach(_.foreach(func => {
      val name = func.name
      val fullName = currentPath + "/" + name
      links += (fullName -> InterFunc(ManualType(currentPath, name)))
      combine(buildHelperFunc(context, func, links, currentPath), errors)
    }))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHelperFunc(context: ResolverContext, func: HelperFunc, links: mutable.Map[String, InterValue], currentPath: String): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val newPath = currentPath + "/" + func.name
    combine(buildContent(context, func.block, links, currentPath), errors)
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
      case stmt: CallObject => combineWithType(buildCallObject(context, stmt, links, currentPath), errors)
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

  private def buildOperation(context: ResolverContext, operation: Operation, links: mutable.Map[String, InterValue], currentPath: String)(implicit `type`: Option[Type] = None): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    operation.content match {
      case Left(value) => combine(buildOperation(context, value, links, currentPath), errors)
      case Right(value) =>
        val backType = combineWithType(buildComplexValue(context, value, links, currentPath), errors)
        operation.expectedType = backType
    }
    operation.next.foreach(op => combine(buildOperation(context, op._2, links, currentPath), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildCallObject(context: ResolverContext, callObject: CallObject, links: mutable.Map[String, InterValue], rootPath: String): Either[List[ResolverError], Option[Type]] = {
    val errors = ListBuffer.empty[ResolverError]
    var `type`: Option[Type] = None
    //val callPath = rootPath + "/" + callObject.statements.map(_.getName).mkString("/")
    // callObject.path = Some(rootPath)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(`type`)
  }

  private def buildComplexValue(context: ResolverContext, value: ComplexValueStatement[_], links: mutable.Map[String, InterValue], currentPath: String)(implicit `type`: Option[Type] = None): Either[List[ResolverError], Option[Type]] = {
    val errors = ListBuffer.empty[ResolverError]
    var backType: Option[Type] = None
    value match {
      case obj: CallObject => backType = combineWithType(buildCallObject(context, obj, links, currentPath), errors)
      case values: MultiValue => println(getClass.getName + "MultiValue Not yet implemented")
      case value: PrimitiveValue[_] => backType = combineWithType(buildPrimitiveValue(context, value, links, currentPath), errors)
      case value: Value => println(getClass.getName + "Value Not yet implemented")
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(backType)
  }

  private def buildPrimitiveValue(context: ResolverContext, value: PrimitiveValue[_], links: mutable.Map[String, InterValue], currentPath: String)(implicit `type`: Option[Type] = None): Either[List[ResolverError], Option[Type]] = {
    value match {
      case entity: EntityValue => buildEntityValue(context, entity, links, currentPath)
      case _ =>
        println(getClass.getName + " Not yet implemented")
        Right(None)
    }
  }

  private def buildEntityValue(context: ResolverContext, value: EntityValue, links: mutable.Map[String, InterValue], currentPath: String)(implicit `type`: Option[Type] = None): Either[List[ResolverError], Option[Type]] = {
    val errors = ListBuffer.empty[ResolverError]
    value.entityType = `type`
    links += (value.getType.getType.toString -> InterEntity(value.getType))
    value.attrs.foreach(_.zipWithIndex.foreach(attr => {
      val interAttr = InterAttr(attr._1.getType)
      if (attr._1.attr.isDefined) links += (`type`.get.getType.toString + "/" + attr._1.attr.get -> interAttr)
      links += (`type`.get.getType.toString + "/" + attr._2 -> interAttr)
    }))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(`type`)
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

  private def combineWithType(res: Either[List[ResolverError], Option[Type]], errors: ListBuffer[ResolverError]): Option[Type] = {
    res match {
      case Left(value) => errors ++= value
        None
      case Right(value) => value
    }
  }

  private def combine(res: Either[List[ResolverError], Unit], errors: ListBuffer[ResolverError]): Unit = {
    res match {
      case Left(value) => errors ++= value
      case Right(_) =>
    }
  }

}
