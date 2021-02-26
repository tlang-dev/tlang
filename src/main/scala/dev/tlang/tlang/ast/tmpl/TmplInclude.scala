package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.TLangString

case class TmplInclude(calls: List[CallObject], var results: List[Either[TLangString, TmplBlockAsValue]] = List()) extends TmplExpression {
  override def deepCopy(): TmplInclude = TmplInclude(calls, results.map {
    case Left(value) => Left(new TLangString(value.getValue))
    case Right(value) => Right(value.deepCopy())
  })
}
