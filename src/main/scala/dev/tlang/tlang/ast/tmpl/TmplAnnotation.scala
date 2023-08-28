package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplAnnotation(context: Option[ContextContent], var name: TmplID, var values: Option[List[TmplAnnotationParam]]) extends TmplContent[TmplAnnotation] with AstContext {
  override def deepCopy(): TmplAnnotation = TmplAnnotation(context, name.deepCopy().asInstanceOf[TmplID],
    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplAnnotation]): Int = 0

  override def getElement: TmplAnnotation = this

  override def getType: String = getClass.getName
}
