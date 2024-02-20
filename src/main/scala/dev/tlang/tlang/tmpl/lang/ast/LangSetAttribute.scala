package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangSetAttribute(context: Null[ContextContent], var name: Option[TmplID], var value: LangOperation) extends TmplNode[LangSetAttribute] {
  override def deepCopy(): LangSetAttribute = LangSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy())


  override def getElement: LangSetAttribute = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangSetAttribute.modelName)),
    Some(List(
      BuildLang.createAttrNull(context, "name",
        name,
        None
      ),
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    )))

  override def toModel: ModelSetEntity = LangSetAttribute.model
}

object LangSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), LangOperation.modelType)),
  )))
}
