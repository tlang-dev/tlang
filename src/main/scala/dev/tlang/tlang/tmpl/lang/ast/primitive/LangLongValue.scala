package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangLong}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangLongValue(context: Null[ContextContent], value: Long) extends LangPrimitiveValue[LangLongValue] with AstContext {

  override def getContext: Null[ContextContent] = context

  override def getElement: LangLongValue = this

  override def getType: Type = LangLongValue.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangLongValue.modelName)),
    Some(List(
      BuildLang.createAttrLong(context, "value", value)
    ))
  )

}

object LangLongValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TLangLong.getType)),
  )))
}
