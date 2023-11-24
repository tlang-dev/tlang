package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.func.TmplAnnotationParam

case class TmplAnnotation(context: Option[ContextContent], var name: TmplID, var values: Option[List[TmplAnnotationParam]]) extends TmplContent[TmplAnnotation] with AstContext {
  override def deepCopy(): TmplAnnotation = TmplAnnotation(context, name.deepCopy().asInstanceOf[TmplID],
    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplAnnotation]): Int = 0

  override def getElement: TmplAnnotation = this

  override def getType: String = getClass.getName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplLangAst.tmplAnnot.name)),
    Some(List())
  )
}
