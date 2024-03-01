package dev.tlang.tlang.ast.model.set

import tlang.internal.ContextContent

case class ModelSetArray(context: Option[ContextContent], array: String) extends ModelSetValueType[ModelSetArray] {


}
