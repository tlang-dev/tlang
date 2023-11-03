package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.TLangParser.{TmplImplContext, TmplPropsContext, TmplTypeContext}
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.tmpl.{TmplImplFor, TmplImplWith}
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource

object BuildLangImpl {

  def buildImpl(resource: ContextResource, impl: TmplImplContext): EntityValue = {
    val context = BuildAst.addContext(resource, impl)
    EntityValue(context,
      Some(ObjType(context, None, TmplImplAst.tmplImpl.name)),
      Some(List(
        BuildLang.buildLangStr(context, "name", impl.name.getText)
      ))
    )
  }

//  def buildFors(resource: ContextResource, props: TmplPropsContext, fors: java.util.List[TmplTypeContext]): EntityValue = {
//    val context = BuildAst.addContext(resource, impl)
//    EntityValue(context,
//      Some(ObjType(context, None, TmplImplAst.tmplImpl.name)),
//      Some(List(
//
//      ))
//    )
//  }
//
//  def buildWiths(resource: ContextResource, props: TmplPropsContext, withs: java.util.List[TmplTypeContext]): EntityValue = {
//
//  }


}
