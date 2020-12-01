package io.sorne.tlang.ast.model.let

import io.sorne.tlang.ast.common.call.CallFuncParam
import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.ast.common.value.TLangType
import io.sorne.tlang.interpreter.Value

case class ModelLetRefFunc(func: HelperFunc, currying: Option[List[CallFuncParam]]) extends ModelLetRefType[ModelLetRefFunc] {
  override def getValue: ModelLetRefFunc = this

  override def getType: String = ModelLetRefFunc.getType

  override def compareTo(value: Value[ModelLetRefFunc]): Int = func.name.compareTo(value.getValue.func.name)
}

object ModelLetRefFunc extends TLangType {
  override def getType: String = "FuncRef"
}
