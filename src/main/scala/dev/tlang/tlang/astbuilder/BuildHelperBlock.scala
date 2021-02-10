package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.helper.{HelperBlock, _}

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

object BuildHelperBlock {

  def build(helperBlock: HelperBlockContext): HelperBlock = {
    val funcs = ListBuffer.empty[HelperFunc]
    helperBlock.helperFuncs.asScala.foreach(func => funcs.addOne(buildFunc(func)))
    HelperBlock(if (funcs.isEmpty) None else Some(funcs.toList))
  }

  def buildFunc(func: HelperFuncContext): HelperFunc = {
    HelperFunc(func.name.getText,
      if (func.currying != null && !func.currying.isEmpty) Some(buildCurrying(func.currying.asScala.toList)) else None,
      if (func.retVals != null && !func.retVals.isEmpty) Some(func.retVals.asScala.toList.map(buildParamType)) else None,
      if (func.body != null) buildContent(func.body) else HelperContent(None))
  }

  def buildCurrying(currying: List[HelperCurryingContext]): List[HelperCurrying] = {
    currying.map(elem => HelperCurrying(buildParams(elem.params.asScala.toList)))
  }

  def buildParams(params: List[HelperParamContext]): List[HelperParam] = {
    params.map(param => HelperParam(if (param.param != null) Some(param.param.getText) else None, buildParamType(param.`type`)))
  }

  def buildParamType(param: HelperParamTypeContext): HelperParamType = {
    param match {
      case param@_ if param.helperObjType() != null => HelperObjType(param.helperObjType().tpye.getText)
      case param@_ if param.helperArrayType() != null => HelperArrayType(param.helperArrayType().tpye.getText)
      case param@_ if param.helperFuncType() != null => buildFuncType(param.helperFuncType())
    }
  }

  def buildFuncType(func: HelperFuncTypeContext): HelperFuncType = {
    HelperFuncType(
      if (func.currying != null && !func.currying.isEmpty) Some(buildCurrying(func.currying.asScala.toList)) else None,
      if (func.retVals != null && !func.retVals.isEmpty) Some(func.retVals.asScala.toList.map(buildParamType)) else None)
  }

  def buildContent(content: HelperContentContext): HelperContent = {
    HelperContent(if (content.content != null && !content.content.isEmpty) Some(BuildHelperStatement.build(content.content.asScala.toList)) else None)
  }

}
