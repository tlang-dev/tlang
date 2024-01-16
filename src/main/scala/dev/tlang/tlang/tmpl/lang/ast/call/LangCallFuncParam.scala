package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangNode}

case class LangCallFuncParam(context: Option[ContextContent], var params: Option[List[LangNode[_]]]) extends LangNode[LangCallFuncParam] {
  override def compareTo(value: Value[LangCallFuncParam]): Int = 0

  override def getElement: LangCallFuncParam = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): LangCallFuncParam = LangCallFuncParam(
    context,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[LangNode[_]])) else None,
  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallFuncParam.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangCallFuncParam.model
}

object LangCallFuncParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
