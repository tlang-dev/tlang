package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplParam(context: Option[ContextContent], var name: TmplID, var `type`: TmplType) extends DeepCopy with AstContext {
  override def deepCopy(): TmplParam = TmplParam(context, name.deepCopy().asInstanceOf[TmplID], `type`.deepCopy())

  override def getContext: Option[ContextContent] = context
}
