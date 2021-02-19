package dev.tlang.tlang.ast.tmpl

case class TmplVar(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var `type`: TmplType, var value: Option[TmplExpression]) extends TmplExpression
