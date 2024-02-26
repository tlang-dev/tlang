package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.createArray
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangUse(context: Null, var parts: List[TmplID], var alias: Option[TmplID] = None) extends TmplNode[LangUse] {
  //  override def deepCopy(): LangUse = LangUse(context, parts.map(_.deepCopy().asInstanceOf[TmplID]),
  //    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[TmplID]) else None)

  override def getElement: LangUse = this

  override def getType: Type = LangUse.modelName


  override def toEntity: EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, LangUse.modelName)),
      Some(List(
        createArray(context, "parts", parts.map(part => part.toEntity)),
        //        BuildLang.createAttrNull(context, "alias",
        //          if (alias.isDefined) Null.of(alias.get.toEntity) else Null.empty(),
        //          None
        //        ),
      )
      ))
  }

  override def getContext: Null = context
}

object LangUse {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("parts"), ModelSetType(Null.empty(), ArrayValue.getType)),
    ModelSetAttribute(Null.empty(), Some("alias"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}

