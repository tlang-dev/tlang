package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.func.TmplFuncParam

case class TmplSpecialBlock(context: Option[ContextContent], var `type`: String, var curries: Option[List[TmplFuncParam]], var content: Option[TmplExprContent[_]]) extends TmplExpression[TmplSpecialBlock] with TmplContent[TmplSpecialBlock] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplSpecialBlock]): Int = 0

  override def deepCopy(): TmplSpecialBlock = TmplSpecialBlock(
    context,
    `type` = `type`,
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    content = if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[TmplExprContent[_]]) else None)

  override def getElement: TmplSpecialBlock = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplSpecialBlock.name)),
    Some(List(
      //        ComplexAttribute(context, Some("parts"),
      //          None, Operation(context, None, Right(ArrayValue(context, Some(pkg.parts.asScala.toList.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.getText)))))))))
      //        )
    )
    ))

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
