package io.sorne.tlang.ast.tmpl.func

import io.sorne.tlang.ast.tmpl.{TmplContent, TmplExpression}

case class TmplFunc(name: String, curries: Option[List[TmplFuncCurry]], content: Option[List[TmplExpression]]) extends TmplExpression with TmplContent
