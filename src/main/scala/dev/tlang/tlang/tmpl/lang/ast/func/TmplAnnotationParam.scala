package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{DeepCopy, TmplID, TmplLangAst, TmplNode, TmplValueType}

case class TmplAnnotationParam(context: Option[ContextContent], var name: Option[TmplID], var value: TmplValueType[_]) extends DeepCopy with TmplNode[TmplAnnotationParam] with AstContext {
  override def deepCopy(): TmplAnnotationParam =
    TmplAnnotationParam(context,
      if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
      value.deepCopy().asInstanceOf[TmplValueType[_]])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplAnnotationParam]): Int = 0

  override def getElement: TmplAnnotationParam = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplAnnotParam.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
