package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangID}

case class LangCallFunc(context: Option[ContextContent], var name: LangID, var currying: Option[List[LangCallFuncParam]]) extends LangCallObjType[LangCallFunc] {
  override def deepCopy(): LangCallFunc = LangCallFunc(context, name.deepCopy().asInstanceOf[LangID],
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangCallFunc]): Int = 0

  override def getElement: LangCallFunc = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallFunc.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangCallFunc.model
}

object LangCallFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, "LangCallFunc", Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
