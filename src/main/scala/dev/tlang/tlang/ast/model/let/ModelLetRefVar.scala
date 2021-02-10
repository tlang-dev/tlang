package dev.tlang.tlang.ast.model.let

import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.interpreter.Value

case class ModelLetRefVar(variable: Value[_]) extends ModelLetRefType[ModelLetRefVar] {
  override def getValue: ModelLetRefVar = this

  override def getType: String = ModelLetRefVar.getType

  override def compareTo(value: Value[ModelLetRefVar]): Int = {
    if (variable.getType == value.getType) variable.asInstanceOf[Value[ModelLetRefVar]].compareTo(value.getValue)
    else -1
  }
}

object ModelLetRefVar extends TLangType {
  override def getType: String = "VarRef"
}
