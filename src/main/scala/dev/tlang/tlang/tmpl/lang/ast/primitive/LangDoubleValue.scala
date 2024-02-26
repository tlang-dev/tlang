package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangDouble}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangDoubleValue(context: Null, value: Double) extends LangPrimitiveValue[LangDoubleValue] with AstContext {

  override def getContext: Null = context

  override def getElement: LangDoubleValue = this

  override def getType: Type = LangDoubleValue.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangDoubleValue.modelName)),
    Some(List(
      BuildLang.createAttrDouble(context, "value", value)
    ))
  )

}

object LangDoubleValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), TLangDouble.getType)),
  )))
}
