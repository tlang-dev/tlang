package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class CallObject(context: Null[ContextContent], statements: List[CallObjectType]) extends ComplexValueStatement[CallObject] with TmplNode[CallObject] {
  override def getElement: CallObject = this

  override def getType: String = CallObject.getType

  override def compareTo(value: Value[CallObject]): Int = 0

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, CallObject.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = CallObject.model

  override def deepCopy(): CallObject = CallObject(context, statements)
}

object CallObject extends TLangType {
  override def getType: String = "CallObject"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("calls"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
