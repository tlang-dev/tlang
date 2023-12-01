package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.TLang.TmplImplContext
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.lang.ast.TmplImplAst

object BuildLangImpl {

  def buildImpl(resource: ContextResource, impl: TmplImplContext): EntityValue = {
    val context = BuildAst.addContext(resource, impl)
    EntityValue(context,
      Some(ObjType(context, None, TmplImplAst.langImpl.name)),
      Some(List(
        BuildLang.createAttrEntity(context, "name", BuildLangValue.buildId(resource, impl.name))
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
