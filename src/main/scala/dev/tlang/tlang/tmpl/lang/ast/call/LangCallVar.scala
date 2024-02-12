package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.Null
import tlang.internal.ContextContent

case class LangCallVar(context: Null[ContextContent], var name: TmplID) extends LangCallObjType[LangCallVar] {
  override def deepCopy(): LangCallVar = LangCallVar(context, name.deepCopy().asInstanceOf[TmplID])

  override def getElement: LangCallVar = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallVar.name)),
    Some(List(BuildLang.createAttrEntity(context, "name", name.toEntity)))
  )

  override def toModel: ModelSetEntity = LangCallVar.model
}

object LangCallVar {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TmplID.name)),
  )))
}