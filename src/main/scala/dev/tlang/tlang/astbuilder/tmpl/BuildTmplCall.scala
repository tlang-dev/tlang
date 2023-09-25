package dev.tlang.tlang.astbuilder.tmpl

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.astbuilder.tmpl.BuildTmplBlock._

import scala.jdk.CollectionConverters._

object BuildTmplCall {

  def buildCallObject(resource: ContextResource, obj: TmplCallObjContext): TmplCallObj = {
    TmplCallObj(addContext(resource, obj), buildProps(resource, obj.props), buildCallObjectType(resource, obj.firstCall), obj.objs.asScala.toList.map(obj => buildCallObjectLink(resource, obj)))
  }

  def buildCallObjectLink(resource: ContextResource, objLink: TmplCallObjLinkContext): TmplCallObjectLink = {
    TmplCallObjectLink(addContext(resource, objLink), objLink.access.getText, buildCallObjectType(resource, objLink.obj))
  }

  def buildCallObjectType(resource: ContextResource, objType: TmplCallObjTypeContext): TmplCallObjType[_] = {
    objType match {
      case array@_ if array.tmplCallArray() != null => buildCallArray(resource, array.tmplCallArray())
      case func@_ if func.tmplCallFunc() != null => buildCallFunc(resource, func.tmplCallFunc())
      case variable@_ if variable.tmplCallVariable() != null => buildCallVar(resource, variable.tmplCallVariable())
      case value@_ if value.tmplPrimitiveValue() != null => buildPrimitive(resource, value.tmplPrimitiveValue())
    }
  }

  def buildCallArray(resource: ContextResource, array: TmplCallArrayContext): TmplCallArray = {
    TmplCallArray(addContext(resource, array), buildId(resource, array.name), buildOperation(resource, array.elem))
  }

  def buildCallVar(resource: ContextResource, variable: TmplCallVariableContext): TmplCallVar = {
    TmplCallVar(addContext(resource, variable), buildId(resource, variable.name))
  }

  def buildCallFunc(resource: ContextResource, func: TmplCallFuncContext): TmplCallFunc = {
    TmplCallFunc(addContext(resource, func), buildId(resource, func.name), if (func.currying != null && !func.currying.isEmpty) Some(buildCallFuncCurrying(resource, func.currying.asScala.toList)) else None)
  }

  def buildCallFuncCurrying(resource: ContextResource, currying: List[TmplCurryParamsContext]): List[TmplCallFuncParam] = {
    currying.map(currying => buildCallFuncParams(resource, currying))
  }

  def buildCallFuncParams(resource: ContextResource, param: TmplCurryParamsContext): TmplCallFuncParam = {
    TmplCallFuncParam(addContext(resource, param), if (param.params != null && !param.params.isEmpty) Some(param.params.asScala.toList.map(param => buildInclSetAttribute(resource, param))) else None)
  }

}
