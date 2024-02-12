package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallRefFuncObject
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core
import tlang.core.{Int, Null}

case class LangCallObj(context: Null[ContextContent], var props: Option[LangProp] = None, var firstCall: LangCallObjType[_], var calls: List[LangCallObjectLink]) extends LangSimpleValueType[LangCallObj] with LangExpression[LangCallObj] {
  override def deepCopy(): LangCallObj = LangCallObj(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    firstCall.deepCopy().asInstanceOf[LangCallObjType[_]],
    calls.map(_.deepCopy()))

  override def compareTo(value: core.Value[LangCallObj]): Int = new Int(0)

  override def getElement: LangCallObj = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallObj.name)),
    Some(List(
      BuildLang.createAttrNull(context, "props",
        if (props.isDefined) Some(props.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "firstCall", firstCall.toEntity),
      BuildLang.createArray(context, "calls", calls.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = LangCallObj.model
}

object LangCallObj {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("props"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("firstCall"), ModelSetType(None, LangCallObjType.name)),
    ModelSetAttribute(None, Some("calls"), ModelSetType(None, ArrayValue.getType)),
  )))
}
