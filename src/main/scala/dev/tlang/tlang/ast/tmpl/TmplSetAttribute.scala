package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplSetAttribute(context: Option[ContextContent], var name: Option[TmplID], var value: TmplValueType) extends DeepCopy with AstContext {
  override def deepCopy(): TmplSetAttribute = TmplSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy().asInstanceOf[TmplValueType])

  override def getContext: Option[ContextContent] = context
}
