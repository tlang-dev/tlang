package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID}

case class LangCallVar(context: Null[ContextContent], var name: TmplID) extends LangCallObjType[LangCallVar] {
//  override def deepCopy(): LangCallVar = LangCallVar(context, name.deepCopy().asInstanceOf[TmplID])

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallVar.modelName)),
    Some(List(BuildLang.createAttrEntity(context, "name", name.toEntity)))
  )

  override def getContext: Null[ContextContent] = context

  override def getElement: LangCallVar = this

  override def getType: Type = LangCallVar.modelName
}

object LangCallVar {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.TYPE)),
  )))
}