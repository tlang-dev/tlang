package dev.tlang.tlang.astbuilder

import io.sorne.tlang.TLangParser.{TmplDoWhileContext, TmplWhileContext}
import io.sorne.tlang.ast.tmpl.loop.{TmplDoWhile, TmplWhile}

object BuildTmplLoop {

  def buildWhile(loop: TmplWhileContext): TmplWhile = {
    TmplWhile(BuildTmplBlock.buildConditionBlock(loop.cond), BuildTmplBlock.buildExprContent(loop.content))
  }

  def buildDoWhile(loop: TmplDoWhileContext): TmplDoWhile = {
    TmplDoWhile(BuildTmplBlock.buildExprContent(loop.content), BuildTmplBlock.buildConditionBlock(loop.cond))
  }



}
