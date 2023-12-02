package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplCallAst, TmplID, TmplLangAst}

case class TmplCallArray(context: Option[ContextContent], var name: TmplID, var elem: TmplOperation) extends TmplCallObjType[TmplCallArray] {
  override def deepCopy(): TmplCallArray = TmplCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallArray]): Int = 0

  override def getElement: TmplCallArray = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplCallAst.tmplCallArray.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
