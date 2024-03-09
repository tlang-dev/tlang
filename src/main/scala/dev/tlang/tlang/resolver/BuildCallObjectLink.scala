package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{AssignVar, ComplexValueType, MultiValue, PrimitiveValue}
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.interpreter.recipe.TLangModuleList
import dev.tlang.tlang.interpreter.value.InterResource
import dev.tlang.tlang.tmpl.AstAnyTmplBlock
import tlang.core.Value
import tlang.internal.DomainBlock

import scala.collection.mutable.ListBuffer

object BuildCallObjectLink {

  def buildCallObjectLink(context: PathContext): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    context.resource.ast.body.foreach(body => combine(buildBody(context, body), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }


  private def buildBody(context: PathContext, domain: DomainBlock): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    domain match {
      case model: ModelBlock => combine(buildModel(context, model), errors)
      case helper: HelperBlock => combine(buildHelper(context, helper), errors)
      case tmpl: AstAnyTmplBlock => combine(buildTmpl(context, tmpl), errors)
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildModel(context: PathContext, model: ModelBlock): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]

    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHelper(context: PathContext, helper: HelperBlock): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    helper.funcs.foreach(_.foreach(func => combine(buildHelperFunc(context, func), errors)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildTmpl(context: PathContext, tmpl: AstAnyTmplBlock): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]

    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildHelperFunc(context: PathContext, func: HelperFunc): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val newContext = context.copy(relatedPath = buildRelPath(context.relatedPath, func.name))
    combine(buildContent(newContext, func.block), errors)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildContent(context: PathContext, content: HelperContent): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    content.content.foreach(_.foreach(stmt => combine(buildStatement(context, stmt), errors)))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildStatement(context: PathContext, statement: HelperStatement): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    statement match {
      case stmt: HelperIf => combine(buildIf(context, stmt), errors)
      case stmt: CallObject => combine(buildCallObject(context, stmt), errors)
      case stmt: HelperFor =>
      case stmt: HelperFunc =>
        val newContext = context.copy(relatedPath = buildRelPath(context.relatedPath, stmt.name))
        buildHelperFunc(newContext, stmt)
      case stmt: Operation => combine(buildOperation(context, stmt), errors)
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

  private def buildIf(context: PathContext, helperIf: HelperIf): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    combine(buildOperation(context, helperIf.condition), errors)
    helperIf.ifTrue.foreach(content => combine(buildContent(context, content), errors))
    helperIf.ifFalse.foreach(content => combine(buildContent(context, content), errors))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildOperation(context: PathContext, op: Operation): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    op.content match {
      case Left(value) => combine(buildOperation(context, value), errors)
      case Right(value) => combine(buildComplexValue(context, value), errors)
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildCallObject(context: PathContext, call: CallObject): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val link = context.getFullPath + "/" + call.statements.head.getName
    //Func or entity level
    testLink(context, call, link) match {
      case Some(value) => call.resolved = Some(value)
      case None =>
        //resource level
        val link = context.getResourcePath + "/" + call.statements.head.getName
        testLink(context, call, link) match {
          case Some(value) => call.resolved = Some(value)
          case None => combine(searchInInternalClasses(context, call), errors)
        }
    }
    combine(buildSubCallObject(context, call, 0), errors)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def searchInInternalClasses(context: PathContext, call: CallObject): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val name = call.statements.head.getName
    context.resource.ast.header.foreach(_.uses.foreach(_.foreach(use => {
      val useName = use.alias.getOrElse(use.parts.last)
      if (name == useName) {
        val fullName = "TLang/" + use.parts.mkString("/")
        val allClasses = TLangModuleList.internalClasses
        val optClass = TLangModuleList.internalClasses.get(fullName)
        if (optClass.isDefined) call.resolved = Some(CallResolved(fullName, fullName, 1, optClass.get))
        else combine(searchInCoreClasses(context, call), errors)
      }
    })))
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildSubCallObject(context: PathContext, call: CallObject, index: Int): Either[List[ResolverError], Unit] = {
    if (index >= call.statements.size) Right(())
    else {
      val errors = ListBuffer.empty[ResolverError]
      call.statements(index) match {
        case callFunc: CallFuncObject => callFunc.currying.foreach(_.foreach(_.params.foreach(_.foreach(param => combine(buildSetAttribute(context, param), errors)))))
        case _ =>
      }
      combine(buildSubCallObject(context, call, index + 1), errors)
      if (errors.nonEmpty) Left(errors.toList)
      else Right(())
    }
  }

  private def searchInCoreClasses(context: PathContext, call: CallObject): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    val fullName = "tlang.core." + call.statements.head.getName.toLowerCase()
    val optClass = TLangModuleList.internalClasses.get(fullName)
    if (optClass.isDefined) call.resolved = Some(CallResolved(fullName, fullName, 1, optClass.get))
    else errors += ResourceNotFound(call.context, call.statements.head.getName)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def testLink(context: PathContext, call: CallObject, link: String): Option[CallResolved] = {
    if (context.links.contains(link)) {
      val value = context.links(link)
      if (!value.isInstanceOf[InterResource]) {
        Some(CallResolved(link, link, 1, value))
      } else if (call.statements.size >= 2) {
        val link2 = context.getFullPath + "/" + call.statements.head.getName + "/" + call.statements(1)
        if (context.links.contains(link)) {
          val value = context.links(link)
          Some(CallResolved(link, link2, 2, value))
        } else None
      } else None
    } else if (call.statements.size >= 2) {
      val link2 = context.getFullPath + "/" + call.statements.head.getName + "/" + call.statements(1)
      if (context.links.contains(link)) {
        val value = context.links(link)
        Some(CallResolved(link, link2, 2, value))
      } else None
    } else None
  }

  def buildComplexValue(context: PathContext, value: ComplexValueStatement[_]): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    value match {
      case obj: CallObject => combine(buildCallObject(context, obj), errors)
      case values: MultiValue =>
      case value: PrimitiveValue[_] =>
      case value: Value =>
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def buildSetAttribute(context: PathContext, attr: SetAttribute): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    combine(buildOperation(context, attr.value), errors)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  def example(context: PathContext, tmpl: AstAnyTmplBlock): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]

    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def buildRelPath(currentRelPath: String, addTo: String): String = {
    if (currentRelPath.isEmpty) addTo
    else currentRelPath + "/" + addTo
  }

  private def combine(res: Either[List[ResolverError], Unit], errors: ListBuffer[ResolverError]): Unit = {
    res match {
      case Left(value) => errors ++= value
      case Right(_) =>
    }
  }

}
