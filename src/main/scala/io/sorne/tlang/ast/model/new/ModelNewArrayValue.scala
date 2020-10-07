package io.sorne.tlang.ast.model.`new`

import io.sorne.tlang.ast.helper.Callable

case class ModelNewArrayValue(attr: Option[String], tbl: Option[List[ModelNewAttribute]]) extends ModelNewValueType
