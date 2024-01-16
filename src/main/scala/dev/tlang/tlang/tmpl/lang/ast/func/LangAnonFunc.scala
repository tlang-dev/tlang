package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangExprContent, LangExpression}

case class LangAnonFunc(context: Option[ContextContent], var curries: Option[List[LangFuncParam]], var content: LangExprContent[_]) extends LangExpression[LangAnonFunc] {
  override def getElement: LangAnonFunc = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): LangAnonFunc = LangAnonFunc(context,
    if (curries.isDefined) Some(curries.get.map(curry => curry.deepCopy())) else None,
    content.deepCopy().asInstanceOf[LangExprContent[_]])

  override def compareTo(value: Value[LangAnonFunc]): Int = 0

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnonFunc.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangAnonFunc.model
}

object LangAnonFunc {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
