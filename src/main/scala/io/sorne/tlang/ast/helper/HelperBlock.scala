package io.sorne.tlang.ast.helper

import io.sorne.tlang.ast.DomainBlock

case class HelperBlock(funcs: Option[List[HelperFunc]]) extends DomainBlock
