package io.sorne.tlang.ast.tmpl.func

import io.sorne.tlang.ast.tmpl.{TmplContent, TmplExpression}

case class TmplFunc(var name: String, var curries: Option[List[TmplFuncCurry]], var content: Option[List[TmplExpression]]) extends TmplExpression with TmplContent
