package io.sorne.tlang.ast.tmpl

case class TmplAnnotation(var name: String, values: Option[List[TmplAnnotationParam]])
