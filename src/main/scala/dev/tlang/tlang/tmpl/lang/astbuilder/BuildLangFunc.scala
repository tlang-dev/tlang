package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.TLangParser
import dev.tlang.tlang.TLangParser.{TmplCurryingContext, TmplFuncContext}
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.ast.TmplFuncAst

object BuildLangFunc {

  def buildFunc(resource: ContextResource, func: TmplFuncContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplFuncAst.tmplFunc.name)),
      Some(List(
        BuildLang.createAttrEntity(context, "name", BuildLangValue.buildId(resource, func.name))
      ))
    )
  }

  def buildTmplFuncCurries(resource: ContextResource, curries: List[TmplCurryingContext]): EntityValue = {
    val context = BuildAst.addContext(resource, curries.head)
    EntityValue(context,
      Some(ObjType(context, None, TmplFuncAst.tmplFunc.name)),
      Some(List(

      ))
    )
  }

  def buildTmplFuncParam(resource: ContextResource, param: TLangParser.TmplCurryingParamContext): EntityValue = {
    val context = BuildAst.addContext(resource, param)
    EntityValue(context,
      Some(ObjType(context, None, TmplFuncAst.tmplFunc.name)),
      Some(List(

      ))
    )
  }

}
