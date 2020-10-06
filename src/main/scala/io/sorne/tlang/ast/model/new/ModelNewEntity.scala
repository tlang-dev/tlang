package io.sorne.tlang.ast.model.`new`

import io.sorne.tlang.ast.model.ModelContent
import io.sorne.tlang.interpreter.Value

case class ModelNewEntity(name: String, `type`: Option[String], params: Option[List[ModelNewAttribute]], attrs: Option[List[ModelNewAttribute]]) extends ModelContent with Value[ModelNewEntity] {
  override def getValue: ModelNewEntity = this

  override def getType: String = `type`.getOrElse(getClass.getName)

  override def compareTo(value: Value[ModelNewEntity]): Int = ???
}
