package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplAnnotation(context: Option[ContextContent], var name: TmplID, var values: Option[List[TmplAnnotationParam]]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplAnnotation = TmplAnnotation(context, name.deepCopy().asInstanceOf[TmplID],
    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context
}
