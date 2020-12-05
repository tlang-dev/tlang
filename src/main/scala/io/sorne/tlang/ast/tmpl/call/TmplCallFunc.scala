package io.sorne.tlang.ast.tmpl.call

case class TmplCallFunc(name:String, currying: Option[List[TmplCurryParam]]) extends TmplCallObjType
