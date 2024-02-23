package dev.tlang.tlang.ast.model.let

import dev.tlang.tlang.ast.common.call.CallFuncParam
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.helper.HelperFunc
import tlang.core.{Null, Type, Value}
import tlang.internal.ContextContent

case class ModelLetRefFunc(context: Null[ContextContent], func: HelperFunc, currying: Option[List[CallFuncParam]]) extends ModelLetRefType[ModelLetRefFunc] {
  override def getElement: ModelLetRefFunc = this

  override def getType: Type = ModelLetRefFunc.getType

}

object ModelLetRefFunc extends TLangType {
  override def getType: Type = ManualType(getClass.getPackageName, "FuncRef")

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
