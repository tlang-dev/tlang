package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent, TmplNode}

case class LangExprBlock(context: Null[ContextContent], var exprs: List[TmplNode[_]]) extends LangExprContent[LangExprBlock] with AstContext {
//  override def deepCopy(): LangExprBlock = LangExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[TmplNode[_]]))

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangExprBlock.modelName)),
    Some(List(
      BuildLang.createArray(context, "exprs", exprs.map(_.toEntity)),
    ))
  )

//  override def toModel: ModelSetEntity = LangExprBlock.model

  override def getContext: Null[ContextContent] = context

  override def getElement: LangExprBlock = this

  override def getType: Type = LangExprBlock.modelName
}

object LangExprBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("exprs"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}