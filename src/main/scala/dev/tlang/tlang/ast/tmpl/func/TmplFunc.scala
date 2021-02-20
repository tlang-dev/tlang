package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.{TmplContent, TmplExpression, TmplProp, TmplType, _}

case class TmplFunc(var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var curries: Option[List[TmplFuncCurry]], var content: Option[TmplExprBlock],
                    var ret: Option[List[TmplType]] = None) extends TmplExpression with TmplContent {
  override def deepCopy(): TmplFunc = TmplFunc(
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    if (content.isDefined) Some(content.get.deepCopy()) else None,
    if (ret.isDefined) Some(ret.get.map(_.deepCopy())) else None)
}
