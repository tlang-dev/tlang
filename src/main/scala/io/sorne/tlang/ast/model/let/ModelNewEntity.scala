package io.sorne.tlang.ast.model.let

import io.sorne.tlang.ast.model.ModelContent

case class ModelNewEntity(name: String, entity: ModelNewEntityValue) extends ModelContent
