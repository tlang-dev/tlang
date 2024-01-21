package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation

case class LangVal(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: LangID, var `type`: Option[LangType], var value: Option[LangOperation], isOptional: Boolean) extends LangExpression[LangVal] with AstContext {
  override def deepCopy(): LangVal = LangVal(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[LangID],
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (value.isDefined) Some(value.get.deepCopy()) else None,
    isOptional
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangVal]): Int = 0

  override def getElement: LangVal = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangVal.name)),
    Some(List(

    ))
  )

  override def toModel: ModelSetEntity = LangVal.model
}

object LangVal {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}