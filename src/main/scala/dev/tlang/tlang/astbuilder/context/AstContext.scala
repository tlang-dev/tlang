package dev.tlang.tlang.astbuilder.context

trait AstContext {
  def getContext: Option[ContextContent]
}
