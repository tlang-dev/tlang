package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.{DeepCopy, TmplNode}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.createArray

case class LangUse(context: Option[ContextContent], var parts: List[TmplID], var alias: Option[TmplID] = None) extends TmplNode[LangUse] with DeepCopy {
  override def deepCopy(): LangUse = LangUse(context, parts.map(_.deepCopy().asInstanceOf[TmplID]),
    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[TmplID]) else None)

  override def compareTo(value: Value[LangUse]): Int = 0

  override def getElement: LangUse = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, LangUse.name)),
      Some(List(
        createArray(context, "parts", parts.map(part => part.toEntity)),
        BuildLang.createAttrNull(context, "alias",
          if (alias.isDefined) Some(alias.get.toEntity) else None,
          None
        ),
      )
      ))
  }

  override def toModel: ModelSetEntity = LangUse.model
}

object LangUse {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, "LangUse", Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("parts"), ModelSetType(None, ArrayValue.getType)),
    ModelSetAttribute(None, Some("alias"), ModelSetType(None, NullValue.name)),
  )))
}

