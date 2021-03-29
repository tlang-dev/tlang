package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplAnnotation(context: Option[ContextContent], var name: String, values: Option[List[TmplAnnotationParam]]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplAnnotation = TmplAnnotation(context, new String(name),
    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context
}
