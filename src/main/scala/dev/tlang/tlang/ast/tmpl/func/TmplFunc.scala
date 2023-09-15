package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplFunc(context: Option[ContextContent], var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var curries: Option[List[TmplFuncCurry]], var content: Option[TmplExprContent[_]],
                    var ret: Option[List[TmplType]] = None, postPros: Option[TmplProp] = None) extends TmplExpression[TmplFunc] with TmplContent[TmplFunc] with AstContext {
  override def deepCopy(): TmplFunc = TmplFunc(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[TmplExprContent[_]]) else None,
    if (ret.isDefined) Some(ret.get.map(_.deepCopy())) else None,
    if (postPros.isDefined) Some(postPros.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplFunc]): Int = 0

  override def getElement: TmplFunc = this

  override def getType: String = getClass.getName
}
