package io.sorne.tlang.ast.model.set

case class ModelSetType(`type`: String, generics: Option[ModelSetGeneric], isArray: Boolean = false) extends ModelSetValueType
