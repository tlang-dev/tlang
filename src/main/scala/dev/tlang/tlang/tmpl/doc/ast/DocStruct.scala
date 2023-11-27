package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplID, TmplLangAst, TmplNode}

case class DocStruct(context: Option[ContextContent], level:String, title: TmplID, content: DocContent) extends DocContentType[DocStruct] {
  override def deepCopy(): DocStruct = DocStruct(context, new String(level), title.deepCopy().asInstanceOf[TmplID], content.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[DocStruct]): Int = 0

  override def getElement: DocStruct = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
