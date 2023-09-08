package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{TmplExpression, TmplProp, TmplSimpleValueType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplCallObj(context: Option[ContextContent], var props: Option[TmplProp] = None, var firstCall: TmplCallObjType[_], var calls: List[TmplCallObjectLink]) extends TmplSimpleValueType[TmplCallObj] with TmplExpression[TmplCallObj] {
  override def deepCopy(): TmplCallObj = TmplCallObj(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    firstCall.deepCopy().asInstanceOf[TmplCallObjType[_]],
    calls.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallObj]): Int = 0

  override def getElement: TmplCallObj = this

  override def getType: String = getClass.getName
}
