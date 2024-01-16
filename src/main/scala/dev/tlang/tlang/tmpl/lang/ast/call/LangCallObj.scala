package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangCallObj(context: Option[ContextContent], var props: Option[LangProp] = None, var firstCall: LangCallObjType[_], var calls: List[LangCallObjectLink]) extends LangSimpleValueType[LangCallObj] with LangExpression[LangCallObj] {
  override def deepCopy(): LangCallObj = LangCallObj(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    firstCall.deepCopy().asInstanceOf[LangCallObjType[_]],
    calls.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangCallObj]): Int = 0

  override def getElement: LangCallObj = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallObj.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "firstCall", firstCall.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangCallObj.model
}

object LangCallObj {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
