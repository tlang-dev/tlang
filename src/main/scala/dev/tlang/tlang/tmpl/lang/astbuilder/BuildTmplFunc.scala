package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.TLang
import dev.tlang.tlang.TLang.{TmplCurryingContext, TmplFuncContext}
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.common.astbuilder.BuildCommonTmpl
import dev.tlang.tlang.tmpl.lang.ast.LangParam
import dev.tlang.tlang.tmpl.lang.ast.func.{LangFunc, LangFuncParam}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock._

import scala.jdk.CollectionConverters._

object BuildTmplFunc {

  def buildFunc(resource: ContextResource, func: TmplFuncContext): LangFunc = {
    val curries =
      if (func.curries != null && !func.curries.isEmpty) buildFuncCurries(resource, func.curries.asScala.toList)
      else None

    val preNames = if (!func.preNames.isEmpty) Some(func.preNames.asScala.toList.map(preName => BuildCommonTmpl.buildId(resource, preName))) else None

    LangFunc(addContext(resource, func), buildAnnotations(resource, func.annots.asScala.toList), buildProps(resource, func.props),
      preNames = preNames,
      BuildCommonTmpl.buildId(resource, func.name), curries,
      if (func.content != null) Some(buildExprContent(resource, func.content)) else None,
      if (func.types != null && !func.types.isEmpty) Some(func.types.asScala.toList.map(t => buildType(resource, t))) else None, buildProps(resource, func.postProps))
  }

  def buildFuncCurries(resource: ContextResource, curries: List[TmplCurryingContext]): Option[List[LangFuncParam]] = {
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

  def buildFuncParam(resource: ContextResource, params: TmplCurryingContext): LangFuncParam = {
    var strType = "NONE"
    var paramList: List[LangParam] = List()
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
    LangFuncParam(addContext(resource, params), if (paramList.nonEmpty) Some(paramList) else None, strType)
  }

  def buildCurryingParam(resource: ContextResource, param: TLang.TmplCurryingParamContext): List[LangParam] = {
    param.params.asScala.toList.map(buildParam(resource, _))
  }

}
