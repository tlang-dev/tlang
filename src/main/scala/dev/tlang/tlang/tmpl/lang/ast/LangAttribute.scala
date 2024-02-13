package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangAttribute(context: Null[ContextContent], var attr: Option[TmplID], var `type`: Option[LangType], var value: LangOperation) extends TmplNode[LangAttribute] {
  override def deepCopy(): LangAttribute = LangAttribute(context,
    if (attr.isDefined) Some(attr.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    value.deepCopy()
  )


  override def compareTo(value: Value[LangAttribute]): Int = 0

  override def getElement: LangAttribute = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAttribute.name)),
    Some(List(
      BuildLang.createAttrNull(context, "attr",
        if (attr.isDefined) Null.of(attr.get.toEntity) else Null.empty(),
        None
      ),
      BuildLang.createAttrNull(context, "tType",
        if (`type`.isDefined) Null.of(`type`.get.toEntity) else Null.empty(),
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
