package io.sorne.tlang.ast.tmpl.call

import io.sorne.tlang.ast.tmpl.{TmplExpression, TmplSimpleValueType}

case class TmplCallObj(calls: List[TmplCallObjType]) extends TmplSimpleValueType with TmplExpression
