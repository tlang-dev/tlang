package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class LangIf(context: Null[ContextContent], cond: LangOperation, content: LangExprContent[_], elseBlock: Option[Either[LangExprContent[_], LangIf]]) extends LangExpression[LangIf] with AstContext {
//  override def deepCopy(): LangIf = LangIf(context, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]],
//    if (elseBlock.isDefined) elseBlock.get match {
//      case Left(value) => Some(Left(value.deepCopy().asInstanceOf[LangExprContent[_]]))
//      case Right(value) => Some(Right(value.deepCopy()))
//    } else None,
//  )

  override def getContext: Null[ContextContent] = context

  override def getElement: LangIf = this

  override def getType: Type = LangIf.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangIf.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
      BuildLang.createAttrEntity(context, "content", content.toEntity),
    ))
  )

//  override def toModel: ModelSetEntity = LangIf.model
}

object LangIf {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("cond"), ModelSetType(Null.empty(), LangOperation.modelType)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), LangExprContent.modelName)),
    ModelSetAttribute(Null.empty(), Some("elseBlock"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
