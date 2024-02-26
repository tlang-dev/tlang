package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetValueType
import dev.tlang.tlang.interpreter.ExecError
import tlang.core
import tlang.core.{Int, Model, Null, Type, Value}
import tlang.internal.ContextContent

class TLangDouble(context: Null, value: Double) extends PrimitiveValue[Double] {

  override def getType: Type = TLangDouble.getType

  override def toString: String = getElement.toString

//  override def deepCopy(): TLangDouble = new TLangDouble(context, value)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TLangDouble.getType)),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = ModelSetEntity(Null.empty(), getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  //  )))

  override def getElement: Double = value

//  override def toModel: Model = ???

  override def getContext: Null = context
}

object TLangDouble extends TLangType {
  override def getType: Type = core.Double.TYPE

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
