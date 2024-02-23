package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID}

case class LangCallArray(context: Null[ContextContent], var name: TmplID, var elem: LangOperation) extends LangCallObjType[LangCallArray] {
//  override def deepCopy(): LangCallArray = LangCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy())

  override def getElement: LangCallArray = this

  override def getType: Type = LangCallArray.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallArray.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrEntity(context, "operation", elem.toEntity),
    ))
  )

  override def getContext: Null[ContextContent] = context
}

object LangCallArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.TYPE)),
    ModelSetAttribute(Null.empty(), Some("operation"), ModelSetType(Null.empty(), LangOperation.modelType)),
  )))
}
