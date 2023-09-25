package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplAnnotationParam(context: Option[ContextContent], var name: Option[TmplID], var value: TmplValueType[_]) extends DeepCopy with TmplNode[TmplAnnotationParam] with AstContext {
  override def deepCopy(): TmplAnnotationParam =
    TmplAnnotationParam(context,
      if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
      value.deepCopy().asInstanceOf[TmplValueType[_]])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplAnnotationParam]): Int = 0

  override def getElement: TmplAnnotationParam = this

  override def getType: String = getClass.getName
}
