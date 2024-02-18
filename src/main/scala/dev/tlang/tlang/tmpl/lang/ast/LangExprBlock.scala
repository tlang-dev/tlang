package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent, TmplNode}

case class LangExprBlock(context: Null[ContextContent], var exprs: List[TmplNode[_]]) extends LangExprContent[LangExprBlock] with AstContext {
  override def deepCopy(): LangExprBlock = LangExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[TmplNode[_]]))

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangExprBlock.name)),
    Some(List(
      BuildLang.createArray(context, "exprs", exprs.map(_.toEntity)),
    ))
  )

  override def toModel: ModelSetEntity = LangExprBlock.model

  override def getContext: Null[ContextContent] = context
}

object LangExprBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("exprs"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}