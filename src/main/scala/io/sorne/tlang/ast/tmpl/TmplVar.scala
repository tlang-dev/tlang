package io.sorne.tlang.ast.tmpl

case class TmplVar(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: String, var `type`: TmplType, var value: TmplExpression) extends TmplExpression
