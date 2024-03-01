package dev.tlang.tlang.ast.model.let

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import tlang.core.{Int, Null, Type, Value}

case class ModelLetRefVar(context: Null, variable: Value) extends ModelLetRefType[ModelLetRefVar] {

  override def getType: Type = ManualType(this.getClass.getPackageName, "VarRef")

  override def compareTo(value: Value): Int = {
    if (variable.getType == value.getType) variable.compareTo(value.getValue)
    else new Int(-1)
  }

  override def getValue: Value = this
}

object ModelLetRefVar extends TLangType {
  override def getType: Type = ManualType(getClass.getPackageName, "VarRef")

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
