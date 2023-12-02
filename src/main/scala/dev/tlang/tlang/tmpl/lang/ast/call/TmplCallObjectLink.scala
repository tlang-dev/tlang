package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{DeepCopy, TmplCallAst, TmplLangAst, TmplNode}

case class TmplCallObjectLink(context: Option[ContextContent], var link: String = ".", var call: TmplCallObjType[_]) extends DeepCopy with TmplNode[TmplCallObjectLink] {
  override def deepCopy(): TmplCallObjectLink = TmplCallObjectLink(context, link, call.deepCopy().asInstanceOf[TmplCallObjType[_]])

  override def compareTo(value: Value[TmplCallObjectLink]): Int = 0

  override def getElement: TmplCallObjectLink = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplCallAst.tmplCallObjLink.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
