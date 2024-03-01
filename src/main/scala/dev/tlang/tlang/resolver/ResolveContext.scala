package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.ModelBlock
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.libraries.Modules
import dev.tlang.tlang.loader.{BuildModuleTree, Module, Resource}
import dev.tlang.tlang.resolver.checker.CheckExistingElement
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.doc.ast.DocBlock
import dev.tlang.tlang.tmpl.lang.ast.{LangBlock, LangBlockAsValue}
import tlang.core.{Null, Value}

import scala.collection.mutable.ListBuffer

object ResolveContext {

  def resolveContext(module: Module): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    if (!Modules.isInternalModule(module)) {
      module.extResources.foreach(_.foreach(module => resolveContext(module._2)))
      module.resources.foreach(resource => {
        val ast = resource._2.ast
        val uses: List[DomainUse] = ast.header match {
          case None => List()
          case Some(header) => header.uses.getOrElse(List())
        }

        CheckExistingElement.checkExistingElement(resource._2) match {
          case Left(errs) => errors.addAll(errs)
          case Right(_) =>
            ast.body.foreach {
              case HelperBlock(_, funcs) =>
//                if (funcs.isNotNull.get())
//                  extractErrors(errors, BrowseFunc.resolveFuncs(funcs.get().getElement.getRecords.toList, module, uses, resource._2))
              case model: ModelBlock => extractErrors(errors, ResolveModel.resolveModel(model, module, uses, resource._2))
//              case tmpl: AnyTmplInterpretedBlock[_] => extractErrors(errors, ResolveTmpl.resolveTmpl(tmpl, module, uses, resource._2))
            }
        }
      })
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }


  def followCall(resource: Resource, sameResource: Boolean, statements: List[CallObjectType], nextStatement: Int, previousNames: List[String], scope: Scope): Either[List[ResolverError], Null] = {


    val callName: Option[String] = statements(nextStatement) match {
      case varObj: CallVarObject => Some(varObj.name)
      case funcObj: CallFuncObject => funcObj.name
      case refFuncObj: CallRefFuncObject => refFuncObj.name
      case _ => None
    }

    callName match {
      case Some(objName) =>
        if (!sameResource) {
          resource.ast.header match {
            case Some(header) => header.exposes match {
              case Some(exposes) => exposes.find(_.name == objName) match {
                case Some(expose) => addInScope(expose.name, findInResource(resource, statements(nextStatement)), previousNames, scope)
                case None => Left(List(new ResolverError("Value is not exposed: " + objName)))
              }
              case None =>
                println("Verify if you have exposed the element")
                Right(Null.empty().asInstanceOf[Null])
            }
            case None => Right(Null.empty().asInstanceOf[Null])
          }
        } else addInScope(objName, findInResource(resource, statements(nextStatement)), previousNames, scope)
      case None => Left(List(new ResolverError("Should be a var or a func")))
    }

  }

  def findInResource(resource: Resource, nextCaller: CallObjectType): Either[List[ResolverError], Null] = {
    nextCaller match {
      case CallFuncObject(_, name, _) => browseBody(name.get, resource)
      case CallVarObject(_, name) => browseBody(name, resource)
      case CallRefFuncObject(_, name, _, _, _) => browseBody(name.get, resource)
      case _ => Right(Null.empty())
    }
  }

  def browseBody(name: String, resource: Resource): Either[List[ResolverError], Null] = {
    val errors = ListBuffer.empty[ResolverError]
    var elem: Null = Null.empty().asInstanceOf[Null]
    resource.ast.body.foreach {
//      case HelperBlock(_, funcs) => if (funcs.isNotNull.get()) {
//        ResolveUtils.findInFuncs(funcs.get.getElement.getRecords.toList, name) match {
//          case Some(func) => elem = Null.of(func)
//          case None =>
//        }
//      }
//      case ModelBlock(_, contents) => if (contents.isDefined) {
//        ResolveUtils.findInVars(contents.get, name) match {
////          case Some(variable) => elem = Null.of(variable.value)
//          case None =>
//        }
//      }
      case tmpl: AnyTmplInterpretedBlock[_] => findInTmpl(tmpl, name) match {
        case Right(value) => elem = value
        case Left(errs) => errors.addAll(errs)
      }
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(elem)
  }

  def findInTmpl(tmpl: AnyTmplInterpretedBlock[_], name: String): Either[List[ResolverError], Null] = {
    val errors = ListBuffer.empty[ResolverError]
    var elem: Null = Null.empty().asInstanceOf[Null]
    tmpl match {
//      case doc: DocBlock => if (doc.name == name) elem = Null.of(LangBlockAsValue(doc.context, doc, Context())).asInstanceOf[Null]
//      case lang: LangBlock => if (lang.name == name) elem = Null.of(LangBlockAsValue(lang.context, lang, Context())).asInstanceOf[Null]
      case _ => println("ResolveContext: TmplBlock type not yet implemented")
    }
    if (errors.nonEmpty) Left(errors.toList)
    else Right(elem)
  }

  def addInScope(lastName: String, elem: Either[List[ResolverError], Null], previousNames: List[String], scope: Scope): Either[List[ResolverError], Null] = {
    elem match {
      case Left(error) => Left(error)
      case Right(value) => if (value.isNotNull.get()) {
        addValueInScope(lastName, value.get, previousNames, scope)
      }
      else Left(List(ResourceNotFound(None, if (previousNames.nonEmpty) BuildModuleTree.createPkg(previousNames.mkString("/"), lastName) else lastName)))
    }
  }

  def addValueInScope(lastName: String, value: Value, previousNames: List[String], scope: Scope): Either[List[ResolverError], Null] = {
    val name = if (previousNames.nonEmpty) BuildModuleTree.createPkg(previousNames.mkString("/"), lastName) else lastName
    val ret = value match {
      case func: HelperFunc => scope.functions.addOne(name, func)
        func
      //      case variable: PrimitiveValue => scope.variables.addOne(name, variable)
      //        variable
      case variable: Operation => scope.variables.addOne(name, variable)
        variable
      case tmpl: LangBlockAsValue => scope.templates.addOne(name, tmpl.block)
        tmpl
    }
//    Right(Null.of(ret).asInstanceOf[Null])
    Right(Null.empty())
  }

  def extractErrors(errors: ListBuffer[ResolverError], result: Either[List[ResolverError], Unit]): Unit = {
    result match {
      case Left(errs) => errors.addAll(errs)
      case Right(_) =>
    }
  }

}
