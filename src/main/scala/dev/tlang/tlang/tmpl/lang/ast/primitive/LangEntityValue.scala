package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangEntityValue(context: Option[ContextContent], var name: Option[LangID], var params: Option[List[LangNode[_]]], var attrs: Option[List[LangNode[_]]]) extends LangPrimitiveValue[LangEntityValue] {
  override def deepCopy(): LangEntityValue = LangEntityValue(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[LangID]) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[LangNode[_]])) else None,
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[LangNode[_]])) else None
  )

  override def compareTo(value: Value[LangEntityValue]): Int = 0

  override def getElement: LangEntityValue = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangEntityValue.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", if (name.isDefined) name.get.toEntity else LangStringID(context, "").toEntity),
      BuildLang.createArray(context, "params", params.map(_.map(_.toEntity)).getOrElse(List())),
      BuildLang.createArray(context, "attrs", attrs.map(_.map(_.toEntity)).getOrElse(List())),
    ))
  )

  override def toModel: ModelSetEntity = LangEntityValue.model
}

object LangEntityValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
