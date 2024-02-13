package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.createArray
import dev.tlang.tlang.tmpl.{DeepCopy, TmplNode}
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangUse(context: Null[ContextContent], var parts: List[TmplID], var alias: Option[TmplID] = None) extends TmplNode[LangUse] with DeepCopy {
  override def deepCopy(): LangUse = LangUse(context, parts.map(_.deepCopy().asInstanceOf[TmplID]),
    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[TmplID]) else None)

  override def compareTo(value: Value[LangUse]): Int = 0

  override def getElement: LangUse = this

  override def getType: String = getClass.getSimpleName


  override def toEntity: EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, LangUse.name)),
      Some(List(
        createArray(context, "parts", parts.map(part => part.toEntity)),
        BuildLang.createAttrNull(context, "alias",
          if (alias.isDefined) Null.of(alias.get.toEntity) else Null.empty(),
          None
        ),
      )
      ))
  }

  override def toModel: ModelSetEntity = LangUse.model
}

object LangUse {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, "LangUse", Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("parts"), ModelSetType(Null.empty(), ArrayValue.getType)),
    ModelSetAttribute(Null.empty(), Some("alias"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}

