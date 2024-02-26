package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class StyleSetAttribute(context: Null, name: Option[TmplID], value: TmplNode[_]) extends StyleAttribute[StyleSetAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, StyleSetAttribute.modelName)),
    Some(List(
//      BuildLang.createAttrNull(context, "name",
//        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
//        None
//      ),
//      BuildLang.createAttrEntity(context, "value", value.toEntity)
    ))
  )

  override def getElement: StyleSetAttribute = this

  override def getType: Type = StyleSetAttribute.modelName

//  override def deepCopy(): StyleSetAttribute = StyleSetAttribute(context,
//    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
//    value.deepCopy().asInstanceOf[TmplNode[_]]
//  )

  override def getContext: Null = context
}

object StyleSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, StyleModel.styleModel.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TmplNode.TYPE)),
  )))
}
