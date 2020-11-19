package io.sorne.tlang.ast.model.let

import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.ast.helper.call.HelperCallFuncParam
import io.sorne.tlang.interpreter.Value
import io.sorne.tlang.interpreter.`type`.TLangType

case class ModelLetRefFunc(func: HelperFunc, currying: Option[List[HelperCallFuncParam]]) extends ModelLetRefType[ModelLetRefFunc] {
  override def getValue: ModelLetRefFunc = this

  override def getType: String = ModelLetRefFunc.getType

  override def compareTo(value: Value[ModelLetRefFunc]): Int = func.name.compareTo(value.getValue.func.name)
}

object ModelLetRefFunc extends TLangType {
  override def getType: String = "FuncRef"
}
