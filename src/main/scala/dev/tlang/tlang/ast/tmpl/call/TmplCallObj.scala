package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplExpression
import dev.tlang.tlang.ast.tmpl.{TmplExpression, TmplSimpleValueType}

case class TmplCallObj(var calls: List[TmplCallObjType]) extends TmplSimpleValueType with TmplExpression