package io.sorne.tlang.ast.tmpl.loop

import io.sorne.tlang.ast.tmpl.{TmplExprBlock, TmplExpression}

case class TmplFor(content: TmplExprBlock) extends TmplExpression
