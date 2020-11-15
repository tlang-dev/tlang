package io.sorne.tlang.ast.model.`new`

import io.sorne.tlang.ast.model.set.ModelSetRefValue

case class ModelNewAttribute(attr: Option[String] = None, value: ModelNewValueType[_]) extends ModelSetRefValue
