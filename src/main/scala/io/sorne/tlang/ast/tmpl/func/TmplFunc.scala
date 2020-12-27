package io.sorne.tlang.ast.tmpl.func

import io.sorne.tlang.ast.tmpl._

case class TmplFunc(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: String, var curries: Option[List[TmplFuncCurry]], var content: Option[List[TmplExpression]], ret: Option[TmplMultiValue] = None) extends TmplExpression with TmplContent
