package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.TLangParser.{TmplCallArrayContext, TmplCallFuncContext, TmplCallObjContext, TmplCallObjLinkContext, TmplCallVariableContext, TmplCurryParamsContext, TmplFuncContext}
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource

object BuildLangCall {

  def buildCallArray(resource: ContextResource, func: TmplCallArrayContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplCallAst.tmplCallArray.name)),
      Some(List(

      ))
    )
  }

  def buildCallFunc(resource: ContextResource, func: TmplCallFuncContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplCallAst.tmplCallFunc.name)),
      Some(List(

      ))
    )
  }

  def buildCallFuncParam(resource: ContextResource, currying: List[TmplCurryParamsContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplCallAst.tmplCallFuncParam.name)),
      Some(List(

      ))
    )
  }

  def buildCallObj(resource: ContextResource, func: TmplCallObjContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplCallAst.tmplCallObj.name)),
      Some(List(

      ))
    )
  }

  def buildCallObjLink(resource: ContextResource, func: TmplCallObjLinkContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplCallAst.tmplCallObjLink.name)),
      Some(List(

      ))
    )
  }

  def buildCallObjVar(resource: ContextResource, func: TmplCallVariableContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplCallAst.tmplCallObjVar.name)),
      Some(List(

      ))
    )
  }
}
