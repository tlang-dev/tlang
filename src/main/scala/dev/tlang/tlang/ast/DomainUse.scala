package dev.tlang.tlang.ast

import tlang.core.Null
import tlang.internal

case class DomainUse(context: Null[internal.ContextContent], parts: List[String], alias: Option[String] = None) extends internal.AstContext {
}
