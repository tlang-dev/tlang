package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangAttribute(context: Option[ContextContent], var attr: Option[TmplID], var `type`: Option[LangType], var value: LangOperation) extends TmplNode[LangAttribute] {
  override def deepCopy(): LangAttribute = LangAttribute(context,
    if (attr.isDefined) Some(attr.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    value.deepCopy()
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangAttribute]): Int = 0

  override def getElement: LangAttribute = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAttribute.name)),
    Some(List(
      BuildLang.createAttrNull(context, "attr",
        if (attr.isDefined) Some(attr.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrNull(context, "tType",
        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangAttribute.model
}

object LangAttribute {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("attr"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("tType"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("value"), ModelSetType(None, LangOperation.name)),
  )))
}
