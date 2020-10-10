package io.sorne.tlang.ast.model.`new`

case class ModelNewAttribute(attr: Option[String] = None, value: ModelNewValueType[_])
