package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID}

case class LangStringValue(context: Null, var value: TmplID) extends LangPrimitiveValue[LangStringValue] {
  //  override def deepCopy(): LangStringValue = LangStringValue(context, value.deepCopy().asInstanceOf[TmplID])


  override def getElement: LangStringValue = this

  override def getType: Type = LangStringValue.modelName

  override def toString: String = value.toString

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangStringValue.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def getContext: Null = context
}

object LangStringValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TmplID.TYPE)),
  )))
}
