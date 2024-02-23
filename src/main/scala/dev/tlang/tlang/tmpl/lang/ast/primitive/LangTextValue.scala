package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent, TmplID}

case class LangTextValue(context: Null[ContextContent], var value: TmplID) extends LangPrimitiveValue[LangTextValue] with AstContext {
//  override def deepCopy(): LangTextValue = LangTextValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Null[ContextContent] = context


  override def getElement: LangTextValue = this

  override def getType: Type = LangTextValue.modelName

  override def toString: String = value.toString

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangTextValue.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

}

object LangTextValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TmplID.TYPE)),
  )))
}