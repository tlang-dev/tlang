package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.TLangString
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplInclude(context: Option[ContextContent], calls: List[CallObject], var results: List[Either[TLangString, TmplBlockAsValue]] = List()) extends TmplExpression with AstContext {
  override def deepCopy(): TmplInclude = TmplInclude(context, calls, results.map {
    case Left(value) => Left(new TLangString(context, value.getValue))
    case Right(value) => Right(value.deepCopy())
  })

  override def getContext: Option[ContextContent] = context
}
