package dev.tlang.tlang.ast.model

import dev.tlang.tlang.ast.DomainBlock

case class ModelBlock(content: Option[List[ModelContent]]) extends DomainBlock
