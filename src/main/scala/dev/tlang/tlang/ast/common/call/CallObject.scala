package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class CallObject(context: Null, statements: List[CallObjectType]) extends ComplexValueStatement[CallObject] with TmplNode[CallObject] {

  override def getType: Type = CallObject.getType

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, CallObject.modelName)),
    Some(List(
    ))
  )

  override def getContext: Null = context

  override def getElement: CallObject = this
}

object CallObject extends TLangType {

  override def getType: Type = ManualType(getClass.getPackageName, name)

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("calls"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
