package io.sorne.tlang.ast.tmpl

case class TmplImpl(var name: String, var fors: Option[List[TmplImplFor]], var content: Option[List[TmplContent]] = None) extends TmplContent
