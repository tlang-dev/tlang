package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplImplFor(context: Option[ContextContent], var name: TmplID) extends DeepCopy with AstContext {
  override def deepCopy(): TmplImplFor = TmplImplFor(context, name.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context
}
