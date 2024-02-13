package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangCallArray(context: Null[ContextContent], var name: TmplID, var elem: LangOperation) extends LangCallObjType[LangCallArray] {
  override def deepCopy(): LangCallArray = LangCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy())

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

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.name)),
    ModelSetAttribute(Null.empty(), Some("operation"), ModelSetType(Null.empty(), LangOperation.name)),
  )))
}
