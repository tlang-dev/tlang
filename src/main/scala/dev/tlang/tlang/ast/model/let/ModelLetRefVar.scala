package dev.tlang.tlang.ast.model.let

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class ModelLetRefVar(context: Option[ContextContent], variable: Value[_]) extends ModelLetRefType[ModelLetRefVar] {
  override def getElement: ModelLetRefVar = this

  override def getType: String = ModelLetRefVar.getType

  override def compareTo(value: Value[ModelLetRefVar]): Int = {
    if (variable.getType == value.getType) variable.asInstanceOf[Value[ModelLetRefVar]].compareTo(value.getElement)
    else -1
  }

  override def getContext: Option[ContextContent] = context
}

object ModelLetRefVar extends TLangType {
  override def getType: String = "VarRef"
}
