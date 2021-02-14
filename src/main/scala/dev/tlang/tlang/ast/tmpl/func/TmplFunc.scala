package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.{TmplContent, TmplExpression, TmplProp, TmplType, _}

case class TmplFunc(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var curries: Option[List[TmplFuncCurry]], var content: Option[TmplExprBlock],
                    var ret: Option[List[TmplType]] = None) extends TmplExpression with TmplContent
