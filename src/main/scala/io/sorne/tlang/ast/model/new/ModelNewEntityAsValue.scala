package io.sorne.tlang.ast.model.`new`

case class ModelNewEntityAsValue(`type`: Option[String], params: Option[List[ModelNewAttribute]], attrs: Option[List[ModelNewAttribute]]) extends ModelNewValueType
