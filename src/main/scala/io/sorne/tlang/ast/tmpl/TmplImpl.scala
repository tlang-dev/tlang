package io.sorne.tlang.ast.tmpl

case class TmplImpl(name: String, fors: Option[List[TmplImplFor]], content: Option[List[TmplContent]] = None) extends TmplContent
