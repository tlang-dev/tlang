package io.sorne.tlang.ast.tmpl.call

case class TmplCallFunc(var name:String, var currying: Option[List[TmplCurryParam]]) extends TmplCallObjType
