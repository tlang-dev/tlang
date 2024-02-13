package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangSetAttribute(context: Null[ContextContent], var name: Option[TmplID], var value: LangOperation) extends TmplNode[LangSetAttribute] {
  override def deepCopy(): LangSetAttribute = LangSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy())


  override def compareTo(value: Value[LangSetAttribute]): Int = 0

  override def getElement: LangSetAttribute = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangSetAttribute.name)),
    Some(List(
      BuildLang.createAttrNull(context, "name",
        if (name.isDefined) Some(name.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    )))

  override def toModel: ModelSetEntity = LangSetAttribute.model
}

object LangSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), LangOperation.name)),
  )))
}
