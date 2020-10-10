package io.sorne.tlang.ast.model.`new`
import io.sorne.tlang.interpreter.Value

case class ModelNewEntityValue(`type`: Option[String], params: Option[List[ModelNewAttribute]] = None, attrs: Option[List[ModelNewAttribute]] = None) extends ModelNewValueType[ModelNewEntityValue] {
  override def getValue: ModelNewEntityValue = this

  override def getType: String = if(`type`.isDefined)`type`.get else getClass.getName

  override def compareTo(value: Value[ModelNewEntityValue]): Int = ???
}
