package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.helper.{HelperBlock, _}
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

object BuildHelperBlock {

  def build(resource: ContextResource, helperBlock: HelperBlockContext): HelperBlock = {
    val funcs = ListBuffer.empty[HelperFunc]
    helperBlock.helperFuncs.asScala.foreach(func => funcs.addOne(buildFunc(resource, func)))
    HelperBlock(addContext(resource, helperBlock), if (funcs.isEmpty) None else Some(funcs.toList))
  }

  def buildFunc(resource: ContextResource, func: HelperFuncContext): HelperFunc = {
    HelperFunc(addContext(resource, func), func.name.getText,
      if (func.currying != null && !func.currying.isEmpty) Some(buildCurrying(resource, func.currying.asScala.toList)) else None,
      if (func.retVals != null && !func.retVals.isEmpty) Some(func.retVals.asScala.toList.map(retVal => buildParamType(resource, retVal))) else None,
      if (func.body != null) buildContent(resource, func.body) else HelperContent(addContext(resource, func), None))
  }

  def buildCurrying(resource: ContextResource, currying: List[HelperCurryingContext]): List[HelperCurrying] = {
    currying.map(elem => HelperCurrying(addContext(resource, elem), buildParams(resource, elem.params.asScala.toList)))
  }

  def buildParams(resource: ContextResource, params: List[HelperParamContext]): List[HelperParam] = {
    params.map(param => HelperParam(addContext(resource, param), if (param.param != null) Some(param.param.getText) else None, buildParamType(resource, param.`type`)))
  }

  def buildParamType(resource: ContextResource, param: HelperParamTypeContext): ValueType = {
    param match {
      case param@_ if param.objType() != null => BuildCommon.buildObjType(resource, param.objType())
      case param@_ if param.arrayType() != null => BuildCommon.buildArrayType(resource, param.arrayType())
      case param@_ if param.helperFuncType() != null => buildFuncType(resource, param.helperFuncType())
    }
  }

  def buildFuncType(resource: ContextResource, func: HelperFuncTypeContext): HelperFuncType = {
    HelperFuncType(None,
      if (func.currying != null && !func.currying.isEmpty) Some(buildCurrying(resource, func.currying.asScala.toList)) else None,
      if (func.retVals != null && !func.retVals.isEmpty) Some(func.retVals.asScala.toList.map(retVal => buildParamType(resource, retVal))) else None)
  }

  def buildContent(resource: ContextResource, content: HelperContentContext): HelperContent = {
    HelperContent(addContext(resource, content), if (content.content != null && !content.content.isEmpty) Some(BuildHelperStatement.build(resource, content.content.asScala.toList)) else None)
  }

}
