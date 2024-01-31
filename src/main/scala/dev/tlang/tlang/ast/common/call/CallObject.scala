package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangNode}

case class CallObject(context: Option[ContextContent], statements: List[CallObjectType]) extends ComplexValueStatement[CallObject] with LangNode[CallObject] {
  override def getElement: CallObject = this

  override def getType: String = CallObject.getType

  override def compareTo(value: Value[CallObject]): Int = 0

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = ???

  override def toModel: ModelSetEntity = CallObject.model

  override def deepCopy(): CallObject = CallObject(context, statements.map(_.deepCopy()))
}

object CallObject extends TLangType {
  override def getType: String = "CallObject"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("calls"), ModelSetType(None, ArrayValue.getType)),
  )))
}
