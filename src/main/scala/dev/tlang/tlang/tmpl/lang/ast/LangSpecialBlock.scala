package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.func.LangFuncParam

case class LangSpecialBlock(context: Option[ContextContent], var `type`: String, var curries: Option[List[LangFuncParam]], var content: Option[LangExprContent[_]]) extends LangExpression[LangSpecialBlock] with LangContent[LangSpecialBlock] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangSpecialBlock]): Int = 0

  override def deepCopy(): LangSpecialBlock = LangSpecialBlock(
    context,
    `type` = `type`,
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    content = if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[LangExprContent[_]]) else None)

  override def getElement: LangSpecialBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangSpecialBlock.name)),
    Some(List(
      //        ComplexAttribute(context, Some("parts"),
      //          None, Operation(context, None, Right(ArrayValue(context, Some(pkg.parts.asScala.toList.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.getText)))))))))
      //        )
    )
    ))

  override def toModel: ModelSetEntity = LangSpecialBlock.model
}

object LangSpecialBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
