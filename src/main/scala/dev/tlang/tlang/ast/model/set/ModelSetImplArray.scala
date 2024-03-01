package dev.tlang.tlang.ast.model.set

import tlang.internal.ContextContent

case class ModelSetImplArray(context: Option[ContextContent], var modelSetEntity: Option[ModelSetEntity]) extends ModelSetValueType[ModelSetImplArray] {

}
