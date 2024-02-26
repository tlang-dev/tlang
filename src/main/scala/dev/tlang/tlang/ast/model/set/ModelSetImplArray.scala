package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{ClassType, ContextContent}

case class ModelSetImplArray(context: Null, var modelSetEntity: Option[ModelSetEntity]) extends ModelSetValueType[ModelSetImplArray] {

}
