package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallObj
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangAffect(context: Option[ContextContent], var variable: LangCallObj, var value: LangOperation) extends LangExpression[LangAffect] with AstContext {
  override def deepCopy(): LangAffect = LangAffect(context, variable.deepCopy(), value.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangAffect]): Int = 0

  override def getElement: LangAffect = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAffect.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "variable", variable.toEntity),
      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangAffect.model
}

object LangAffect {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("variable"), ModelSetType(None, LangCallObj.name)),
    ModelSetAttribute(None, Some("value"), ModelSetType(None, LangOperation.name)),
  )))
}
