package dev.tlang.tlang.ast.model.set

case class ModelSetRef(refs: List[String], currying: Option[List[ModelSetRefCurrying]]) extends ModelSetValueType with ModelSetRefValue