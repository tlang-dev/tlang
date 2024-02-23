package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Null, Type, Value}
import tlang.internal.ContextContent

case class ArrayValue(context: Null[ContextContent], tbl: Option[List[ComplexAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getElement: ArrayValue = this

  override def getType: Type = ArrayValue.getType

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, ArrayValue.getType)),
    Some(List())
  )

  override def getContext: Null[ContextContent] = context
}

object ArrayValue extends TLangType {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  override def getType: Type = ManualType(getClass.getPackageName, name)

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)
}
