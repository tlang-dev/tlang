package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.common.astbuilder.BuildCommonTmpl
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock._
import tlang.internal.ContextResource

import scala.jdk.CollectionConverters._

object BuildTmplCall {

  def buildCallObject(resource: ContextResource, obj: TmplCallObjContext): LangCallObj = {
    LangCallObj(addContext(resource, obj), buildProps(resource, obj.props), buildCallObjectType(resource, obj.firstCall), obj.objs.asScala.toList.map(obj => buildCallObjectLink(resource, obj)))
  }

  def buildCallObjectLink(resource: ContextResource, objLink: TmplCallObjLinkContext): LangCallObjectLink = {
    LangCallObjectLink(addContext(resource, objLink), objLink.access.getText, buildCallObjectType(resource, objLink.obj))
  }

  def buildCallObjectType(resource: ContextResource, objType: TmplCallObjTypeContext): LangCallObjType[_] = {
    objType match {
      case array@_ if array.tmplCallArray() != null => buildCallArray(resource, array.tmplCallArray())
      case func@_ if func.tmplCallFunc() != null => buildCallFunc(resource, func.tmplCallFunc())
      case variable@_ if variable.tmplCallVariable() != null => buildCallVar(resource, variable.tmplCallVariable())
      case value@_ if value.tmplPrimitiveValue() != null => buildPrimitive(resource, value.tmplPrimitiveValue())
    }
  }

  def buildCallArray(resource: ContextResource, array: TmplCallArrayContext): LangCallArray = {
    LangCallArray(addContext(resource, array), BuildCommonTmpl.buildId(resource, array.name), buildOperation(resource, array.elem))
  }

  def buildCallVar(resource: ContextResource, variable: TmplCallVariableContext): LangCallVar = {
    LangCallVar(addContext(resource, variable), BuildCommonTmpl.buildId(resource, variable.name))
  }

  def buildCallFunc(resource: ContextResource, func: TmplCallFuncContext): LangCallFunc = {
    LangCallFunc(addContext(resource, func), BuildCommonTmpl.buildId(resource, func.name), if (func.currying != null && !func.currying.isEmpty) Some(buildCallFuncCurrying(resource, func.currying.asScala.toList)) else None)
  }

  def buildCallFuncCurrying(resource: ContextResource, currying: List[TmplCurryParamsContext]): List[LangCallFuncParam] = {
    currying.map(currying => buildCallFuncParams(resource, currying))
  }

  def buildCallFuncParams(resource: ContextResource, param: TmplCurryParamsContext): LangCallFuncParam = {
    LangCallFuncParam(addContext(resource, param), if (param.params != null && !param.params.isEmpty) Some(param.params.asScala.toList.map(param => buildInclSetAttribute(resource, param))) else None)
  }

}
