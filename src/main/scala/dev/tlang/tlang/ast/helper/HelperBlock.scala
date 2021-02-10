package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.DomainBlock

case class HelperBlock(funcs: Option[List[HelperFunc]]) extends DomainBlock
