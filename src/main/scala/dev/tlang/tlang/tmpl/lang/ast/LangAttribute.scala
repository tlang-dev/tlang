package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.Null
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangAttribute(context: Null[ContextContent], var attr: Option[TmplID], var `type`: Option[LangType], var value: LangOperation) extends TmplNode[LangAttribute] {
  override def deepCopy(): LangAttribute = LangAttribute(context,
    if (attr.isDefined) Some(attr.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    value.deepCopy()
  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAttribute.name)),
    Some(List(
      BuildLang.createAttrNull(context, "attr",
        attr,
        None
      ),
      BuildLang.createAttrNull(context, "tType",
        `type`,
        None
      ),
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangAttribute.model
}

object LangAttribute {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("attr"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), LangOperation.name)),
  )))
}
