package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallObj
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangAffect(context: Null, var variable: LangCallObj, var value: LangOperation) extends LangExpression[LangAffect] with AstContext {
//  override def deepCopy(): LangAffect = LangAffect(context, variable.deepCopy(), value.deepCopy())

  override def getContext: Null = context

  override def getElement: LangAffect = this

  override def getType: Type = LangAffect.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAffect.modelName)),
    Some(List(
//      BuildLang.createAttrEntity(context, "variable", variable.toEntity),
//      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

//  override def toModel: ModelSetEntity = LangAffect.model
}

object LangAffect {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("variable"), ModelSetType(Null.empty(), LangCallObj.modelName)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), LangOperation.modelType)),
  )))
}
