package dev.tlang.tlang.astbuilder.tmpl

import dev.tlang.tlang.TLangParser
import dev.tlang.tlang.TLangParser.{TmplCurryingContext, TmplFuncContext}
import dev.tlang.tlang.ast.tmpl.TmplParam
import dev.tlang.tlang.ast.tmpl.func.{TmplFunc, TmplFuncParam}
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.astbuilder.tmpl.BuildTmplBlock._

import scala.jdk.CollectionConverters._

object BuildTmplFunc {

  def buildFunc(resource: ContextResource, func: TmplFuncContext): TmplFunc = {
    val curries =
      if (func.curries != null && !func.curries.isEmpty) buildFuncCurries(resource, func.curries.asScala.toList)
      else None

    val preNames = if (!func.preNames.isEmpty) Some(func.preNames.asScala.toList.map(preName => buildId(resource, preName))) else None

    TmplFunc(addContext(resource, func), buildAnnotations(resource, func.annots.asScala.toList), buildProps(resource, func.props),
      preNames = preNames,
      buildId(resource, func.name), curries,
      if (func.content != null) Some(buildExprContent(resource, func.content)) else None,
      if (func.types != null && !func.types.isEmpty) Some(func.types.asScala.toList.map(t => buildType(resource, t))) else None, buildProps(resource, func.postProps))
  }

  def buildFuncCurries(resource: ContextResource, curries: List[TmplCurryingContext]): Option[List[TmplFuncParam]] = {
    //    val params = ListBuffer.empty[TmplFuncParam]
    if (curries != null && curries.nonEmpty) Some(curries.map(param => buildFuncParam(resource, param)))
    else None
    //    if(params.nonEmpty) Some(params.toList) else None
  }

  //  def buildFuncCurries(resource: ContextResource, curry: TmplCurryingContext): TmplFuncParam = {
  //    TmplFuncParam(addContext(resource, curry), buildFuncCurryParam(resource, curry, ))
  //  }

  /* def buildFuncCurryParam(resource: ContextResource, curries: TmplCurryingContext): Option[List[TmplParam]] = {

   }*/

  def buildFuncParam(resource: ContextResource, params: TmplCurryingContext): TmplFuncParam = {
    var strType = "NONE"
    var paramList: List[TmplParam] = List()
    params.params.asScala.toList.foreach(param => {
      paramList = param match {
        case attr@_ if attr.tmplCurryingParam() != null => {
          strType = "ATTR"
          buildCurryingParam(resource, attr.tmplCurryingParam())
        }
        case manda@_ if manda.tmplMandatoryParams() != null => {
          strType = "MAND"
          buildCurryingParam(resource, manda.tmplMandatoryParams().param)
        }
        case pos@_ if pos.tmplPositionParams() != null => {
          strType = "POS"
          buildCurryingParam(resource, pos.tmplPositionParams().param)
        }
        case _ => List()
      }
    })
    TmplFuncParam(addContext(resource, params), if (paramList.nonEmpty) Some(paramList) else None, strType)
  }

  def buildCurryingParam(resource: ContextResource, param: TLangParser.TmplCurryingParamContext): List[TmplParam] = {
    param.params.asScala.toList.map(buildParam(resource, _))
  }

}
