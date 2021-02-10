package dev.tlang.tlang.ast.tmpl

case class TmplAnnotation(var name: String, values: Option[List[TmplAnnotationParam]])
