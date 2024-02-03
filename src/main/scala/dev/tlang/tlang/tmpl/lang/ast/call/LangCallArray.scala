package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangCallArray(context: Option[ContextContent], var name: TmplID, var elem: LangOperation) extends LangCallObjType[LangCallArray] {
  override def deepCopy(): LangCallArray = LangCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangCallArray]): Int = 0

  override def getElement: LangCallArray = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallArray.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrEntity(context, "operation", elem.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangCallArray.model
}

object LangCallArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TmplID.name)),
    ModelSetAttribute(None, Some("operation"), ModelSetType(None, LangOperation.name)),
  )))
}
