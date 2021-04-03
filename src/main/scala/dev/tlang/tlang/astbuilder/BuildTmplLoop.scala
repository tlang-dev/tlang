package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser.{TmplDoWhileContext, TmplWhileContext}
import dev.tlang.tlang.ast.tmpl.loop.{TmplDoWhile, TmplWhile}
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource

object BuildTmplLoop {

  def buildWhile(resource: ContextResource, loop: TmplWhileContext): TmplWhile = {
    TmplWhile(addContext(resource, loop), BuildTmplBlock.buildOperation(resource, loop.cond), BuildTmplBlock.buildExprContent(resource, loop.content))
  }

  def buildDoWhile(resource: ContextResource, loop: TmplDoWhileContext): TmplDoWhile = {
    TmplDoWhile(addContext(resource, loop), BuildTmplBlock.buildExprContent(resource, loop.content), BuildTmplBlock.buildOperation(resource, loop.cond))
  }

}
