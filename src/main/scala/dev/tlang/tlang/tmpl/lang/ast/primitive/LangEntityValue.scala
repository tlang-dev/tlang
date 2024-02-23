package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode, TmplStringId}

case class LangEntityValue(context: Null[ContextContent], var name: Option[TmplID], var params: Option[List[TmplNode[_]]], var attrs: Option[List[TmplNode[_]]]) extends LangPrimitiveValue[LangEntityValue] {
//  override def deepCopy(): LangEntityValue = LangEntityValue(context,
//    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
//    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
//    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None
//  )

  override def getElement: LangEntityValue = this

  override def getType: Type = LangEntityValue.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangEntityValue.modelName)),
    Some(List(
//      BuildLang.createAttrEntity(context, "name", if (name.isDefined) name.get.toEntity else new TmplStringId(context, "").toEntity),
      BuildLang.createArray(context, "params", params.map(_.map(_.toEntity)).getOrElse(List())),
      BuildLang.createArray(context, "attrs", attrs.map(_.map(_.toEntity)).getOrElse(List())),
    ))
  )

  override def getContext: Null[ContextContent] = context
}

object LangEntityValue {

  val name: String = "LangEntity"

  val modelName: Type = ManualType(getClass.getPackageName, name)


  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("attrs"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
