package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID, TmplStringId}

case class LangEntityValue(context: Null[ContextContent], var name: Option[TmplID], var params: Option[List[TmplNode[_]]], var attrs: Option[List[TmplNode[_]]]) extends LangPrimitiveValue[LangEntityValue] {
  override def deepCopy(): LangEntityValue = LangEntityValue(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None
  )

  override def compareTo(value: Value[LangEntityValue]): Int = 0

  override def getElement: LangEntityValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangEntityValue.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", if (name.isDefined) name.get.toEntity else new TmplStringId(context, "").toEntity),
      BuildLang.createArray(context, "params", params.map(_.map(_.toEntity)).getOrElse(List())),
      BuildLang.createArray(context, "attrs", attrs.map(_.map(_.toEntity)).getOrElse(List())),
    ))
  )

  override def toModel: ModelSetEntity = LangEntityValue.model
}

object LangEntityValue {

  val name: String = "LangEntity"

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("attrs"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}
