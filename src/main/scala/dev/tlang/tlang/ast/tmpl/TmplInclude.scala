package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.TLangString

case class TmplInclude(calls: List[CallObject], var results: List[Either[TLangString, TmplBlockAsValue]] = List()) extends TmplExpression
