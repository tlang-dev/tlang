package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangWhile(context: Null[ContextContent], cond: LangOperation, content: LangExprContent[_]) extends LangExpression[LangWhile] with AstContext {
//  override def deepCopy(): LangWhile =
//    LangWhile(context, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]])

  override def getElement: LangWhile = this

  override def getType: Type = LangWhile.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangWhile.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
      BuildLang.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def getContext: Null[ContextContent] = context
}

object LangWhile {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("cond"), ModelSetType(Null.empty(), LangOperation.modelType)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), LangExprContent.modelName)),
  )))
}
