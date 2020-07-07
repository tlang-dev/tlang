package io.sorne.tlang.ast.tmpl.func

import io.sorne.tlang.ast.tmpl.TmplImplContent

case class TmplFunc(name:String, curries:Option[List[TmplFuncCurry]]) extends TmplImplContent{

}
