package dev.tlang.tlang.ast.model.let

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.call.CallFuncParam
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.interpreter.Value

case class ModelLetRefFunc(func: HelperFunc, currying: Option[List[CallFuncParam]]) extends ModelLetRefType[ModelLetRefFunc] {
  override def getValue: ModelLetRefFunc = this

  override def getType: String = ModelLetRefFunc.getType

  override def compareTo(value: Value[ModelLetRefFunc]): Int = func.name.compareTo(value.getValue.func.name)
}

object ModelLetRefFunc extends TLangType {
  override def getType: String = "FuncRef"
}
