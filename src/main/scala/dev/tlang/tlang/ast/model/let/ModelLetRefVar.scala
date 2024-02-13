package dev.tlang.tlang.ast.model.let

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import tlang.core.{Null, Type, Value}
import tlang.internal.ContextContent

case class ModelLetRefVar(context: Null[ContextContent], variable: Value[_]) extends ModelLetRefType[ModelLetRefVar] {
  override def getElement: ModelLetRefVar = this

  override def getType: Type = ManualType(this.getClass.getPackageName, "VarRef")

  override def compareTo(value: Value[ModelLetRefVar]): Int = {
    if (variable.getType == value.getType) variable.asInstanceOf[Value[ModelLetRefVar]].compareTo(value.getElement)
    else -1
  }

}

object ModelLetRefVar extends TLangType {
  override def getType: String = "VarRef"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
