package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangBoolValue(context: Null[ContextContent], value: Boolean) extends LangPrimitiveValue[LangBoolValue] with AstContext {

  override def getContext: Null[ContextContent] = context


  override def getElement: LangBoolValue = this

  override def getType: Type = LangBoolValue.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangBoolValue.modelName)),
    Some(List(
      BuildLang.createAttrBool(context, "value", value)
    ))
  )

}

object LangBoolValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
}
