package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class StyleInclude(context: Null, call: NativeType[CallObject]) extends StyleAttribute[StyleInclude] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, StyleInclude.modelName)),
    Some(List(
//      BuildLang.createAttrEntity(context, "call", call.toEntity),
    ))
  )

//  override def toModel: ModelSetEntity = StyleInclude.model

  override def getElement: StyleInclude = this

  override def getType: Type = StyleInclude.modelName

//  override def deepCopy(): StyleInclude = StyleInclude(context, call.deepCopy().asInstanceOf[NativeType[CallObject]])

  override def getContext: Null = context
}

object StyleInclude {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, StyleModel.styleModel.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("call"), ModelSetType(Null.empty(), NativeType.modelName)),
  )))
}
