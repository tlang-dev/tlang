package io.sorne.tlang.ast.model

import io.sorne.tlang.ast.DomainBlock

case class ModelBlock(content: Option[List[ModelContent]]) extends DomainBlock
