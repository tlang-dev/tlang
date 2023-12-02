package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class TmplCallObj(context: Option[ContextContent], var props: Option[TmplProp] = None, var firstCall: TmplCallObjType[_], var calls: List[TmplCallObjectLink]) extends TmplSimpleValueType[TmplCallObj] with TmplExpression[TmplCallObj] {
  override def deepCopy(): TmplCallObj = TmplCallObj(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    firstCall.deepCopy().asInstanceOf[TmplCallObjType[_]],
    calls.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallObj]): Int = 0

  override def getElement: TmplCallObj = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplCallAst.langCallObj.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "firstCall", firstCall.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
