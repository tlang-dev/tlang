package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.model.ModelContent
import io.sorne.tlang.ast.model.ModelContent

case class ModelSetEntity(name: String, params: Option[List[ModelSetAttribute]], attrs: Option[List[ModelSetAttribute]]) extends ModelContent
