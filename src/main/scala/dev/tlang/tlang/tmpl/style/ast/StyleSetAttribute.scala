package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class StyleSetAttribute(context: Option[ContextContent], name: Option[TmplID], value: TmplNode[_]) extends StyleAttribute[StyleSetAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrNull(context, "name",
        if (name.isDefined) Some(name.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "value", value.toEntity)
    ))
  )

  override def toModel: ModelSetEntity = StyleSetAttribute.model

  override def compareTo(value: Value[StyleSetAttribute]): Int = 0

  override def getElement: StyleSetAttribute = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleSetAttribute = StyleSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
    value.deepCopy().asInstanceOf[TmplNode[_]]
  )

  override def getContext: Option[ContextContent] = context
}

object StyleSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
    ModelSetAttribute(None, Some("name"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("value"), ModelSetType(None, TmplNode.name)),
  )))
}
