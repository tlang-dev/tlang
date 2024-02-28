package dev.tlang.tlang.tmpl

import tlang.core.Type
import tlang.internal.ContextContent

case class AstEntityAttr(context: Option[ContextContent], name: Option[String], var `type`: Option[Type] = None, value: Option[AstValue] = None)
