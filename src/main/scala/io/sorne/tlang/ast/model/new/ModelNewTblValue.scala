package io.sorne.tlang.ast.model.`new`

case class ModelNewTblValue(attr: Option[String], tbl: Option[List[ModelNewAttribute]]) extends ModelNewValueType
