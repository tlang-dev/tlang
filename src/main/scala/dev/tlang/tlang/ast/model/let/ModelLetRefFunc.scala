package dev.tlang.tlang.ast.model.let

import dev.tlang.tlang.ast.common.call.CallFuncParam
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class ModelLetRefFunc(context: Option[ContextContent], func: HelperFunc, currying: Option[List[CallFuncParam]]) extends ModelLetRefType[ModelLetRefFunc] {
  override def getElement: ModelLetRefFunc = this

  override def getType: String = ModelLetRefFunc.getType

  override def compareTo(value: Value[ModelLetRefFunc]): Int = func.name.compareTo(value.getElement.func.name)

  override def getContext: Option[ContextContent] = context
}

object ModelLetRefFunc extends TLangType {
  override def getType: String = "FuncRef"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
