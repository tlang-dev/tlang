package dev.tlang.tlang.tmpl

import tlang.core.Type
import tlang.internal.ContextContent

trait AstContext {

  def getContext: Option[ContextContent]

  def getType: Type

}
