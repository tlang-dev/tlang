package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplExprContent, TmplExpression, TmplFuncAst}

case class TmplAnonFunc(context: Option[ContextContent], var curries: Option[List[TmplFuncParam]], var content: TmplExprContent[_]) extends TmplExpression[TmplAnonFunc] {
  override def getElement: TmplAnonFunc = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): TmplAnonFunc = TmplAnonFunc(context,
    if (curries.isDefined) Some(curries.get.map(curry => curry.deepCopy())) else None,
    content.deepCopy().asInstanceOf[TmplExprContent[_]])

  override def compareTo(value: Value[TmplAnonFunc]): Int = 0

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplFuncAst.langAnonFunc.name)),
    Some(List())
  )
}
