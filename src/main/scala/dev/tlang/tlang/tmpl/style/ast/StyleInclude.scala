package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class StyleInclude(context: Option[ContextContent], call: NativeType[CallObject]) extends StyleAttribute[StyleInclude] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "call", call.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = StyleInclude.model

  override def compareTo(value: Value[StyleInclude]): Int = 0

  override def getElement: StyleInclude = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleInclude = StyleInclude(context, call.deepCopy().asInstanceOf[NativeType[CallObject]])

  override def getContext: Option[ContextContent] = context
}

object StyleInclude {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
    ModelSetAttribute(None, Some("call"), ModelSetType(None, NativeType.name)),
  )))
}
