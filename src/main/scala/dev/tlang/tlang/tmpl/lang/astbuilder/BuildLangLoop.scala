package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.TLang.{TmplDoWhileContext, TmplForContext, TmplWhileContext}
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.context.ContextResource

object BuildLangLoop {

  def buildFor(resource: ContextResource, impl: TmplForContext): EntityValue = {
    val context = BuildAst.addContext(resource, impl)
    EntityValue(context,
      Some(ObjType(context, None, TmplLoopAst.tmplFor.name)),
      Some(List(

      ))
    )
  }

  def buildWhile(resource: ContextResource, impl: TmplWhileContext): EntityValue = {
    val context = BuildAst.addContext(resource, impl)
    EntityValue(context,
      Some(ObjType(context, None, TmplLoopAst.tmplWhile.name)),
      Some(List(

      ))
    )
  }

  def buildDoWhile(resource: ContextResource, impl: TmplDoWhileContext): EntityValue = {
    val context = BuildAst.addContext(resource, impl)
    EntityValue(context,
      Some(ObjType(context, None, TmplLoopAst.tmplDoWhile.name)),
      Some(List(

      ))
    )
  }
}
