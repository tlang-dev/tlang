package io.sorne.tlang.ast.model.let

import io.sorne.tlang.interpreter.Value
import io.sorne.tlang.interpreter.`type`.TLangType

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
