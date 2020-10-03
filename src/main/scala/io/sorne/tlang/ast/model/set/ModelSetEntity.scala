package io.sorne.tlang.ast.model.set

import io.sorne.tlang.ast.model.ModelContent

case class ModelSetEntity(name: String, params:Option[List[ModelSetAttribute]]) extends ModelContent
