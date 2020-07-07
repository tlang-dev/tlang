package io.sorne.tlang.ast.model

case class ModelSetEntity(name: String, params:Option[List[ModelSetAttribute]]) extends ModelContent
