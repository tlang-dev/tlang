package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangNode, LangParam}

case class LangFuncParam(context: Option[ContextContent], params: Option[List[LangParam]], var `type`: String) extends LangNode[LangFuncParam] {
  override def compareTo(value: Value[LangFuncParam]): Int = 0

  override def getElement: LangFuncParam = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): LangFuncParam = LangFuncParam(context,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
    `type`
  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangFuncParam.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangFuncParam.model
}

object LangFuncParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
