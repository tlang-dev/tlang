package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.{TmplContent, TmplExpression, TmplProp, TmplType}
import io.sorne.tlang.ast.tmpl._

case class TmplFunc(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var curries: Option[List[TmplFuncCurry]], var content: Option[TmplExprBlock],
                    ret: Option[List[TmplType]] = None) extends TmplExpression with TmplContent
