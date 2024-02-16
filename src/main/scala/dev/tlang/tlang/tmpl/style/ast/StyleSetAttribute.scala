package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.Value
import tlang.internal.{ContextContent, TmplID, TmplNode}
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}

case class StyleSetAttribute(context: Null[ContextContent], name: Option[TmplID], value: TmplNode[_]) extends StyleAttribute[StyleSetAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrNull(context, "name",
        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
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

  override def getContext: Null[ContextContent] = context
}

object StyleSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, StyleModel.styleModel.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TmplNode.name)),
  )))
}
