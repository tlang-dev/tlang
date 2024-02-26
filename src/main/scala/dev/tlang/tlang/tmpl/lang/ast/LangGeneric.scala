package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class LangGeneric(context: Null, var types: List[LangType]) extends TmplNode[LangGeneric] {
//  override def deepCopy(): LangGeneric = LangGeneric(context, types.map(_.deepCopy()))

  override def getContext: Null = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangGeneric.modelName)),
    Some(List(
//      BuildLang.createArray(context, "types", types.map(_.toEntity))
    ))
  )

//  override def toModel: ModelSetEntity = LangGeneric.model

  override def getElement: LangGeneric = this

  override def getType: Type = LangGeneric.modelName
}

object LangGeneric {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("types"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
