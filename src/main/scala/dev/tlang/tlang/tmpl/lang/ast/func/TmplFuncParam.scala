package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplFuncAst, TmplLangAst, TmplNode, TmplParam}

case class TmplFuncParam(context: Option[ContextContent], params: Option[List[TmplParam]], var `type`: String) extends TmplNode[TmplFuncParam] {
  override def compareTo(value: Value[TmplFuncParam]): Int = 0

  override def getElement: TmplFuncParam = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): TmplFuncParam = TmplFuncParam(context,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
    `type`
  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplFuncAst.tmplFuncParam.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
