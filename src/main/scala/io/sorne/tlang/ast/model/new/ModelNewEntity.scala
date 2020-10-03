package io.sorne.tlang.ast.model.`new`

import io.sorne.tlang.ast.model.ModelContent

case class ModelNewEntity(name: String, `type`: Option[String], params: Option[List[ModelNewAttribute]], attrs: Option[List[ModelNewAttribute]]) extends ModelContent
