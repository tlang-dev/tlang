package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.TLang.{TmplDoWhileContext, TmplWhileContext}
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.tmpl.lang.ast.loop.{LangDoWhile, LangWhile}
import tlang.internal.ContextResource

object BuildTmplLoop {

  def buildWhile(resource: ContextResource, loop: TmplWhileContext): LangWhile = {
    LangWhile(addContext(resource, loop), BuildTmplBlock.buildOperation(resource, loop.cond), BuildTmplBlock.buildExprContent(resource, loop.content))
  }

  def buildDoWhile(resource: ContextResource, loop: TmplDoWhileContext): LangDoWhile = {
    LangDoWhile(addContext(resource, loop), BuildTmplBlock.buildExprContent(resource, loop.content), BuildTmplBlock.buildOperation(resource, loop.cond))
  }

}
