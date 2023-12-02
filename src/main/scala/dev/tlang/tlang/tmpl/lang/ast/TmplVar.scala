package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.TmplOperation

case class TmplVar(context: Option[ContextContent], var annots: Option[List[TmplAnnotation]] = None, var props: Option[TmplProp] = None, var name: TmplID, var `type`: Option[TmplType], var value: Option[TmplOperation], isOptional: Boolean) extends TmplExpression[TmplVar] with AstContext {
  override def deepCopy(): TmplVar = TmplVar(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (value.isDefined) Some(value.get.deepCopy()) else None,
    isOptional
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplVar]): Int = 0

  override def getElement: TmplVar = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langVar.name)),
    Some(List(

    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
