package dev.tlang.tlang.ast.model.set

case class ModelSetAttribute(attr: Option[String], value: ModelSetValueType) extends ModelSetValueType
