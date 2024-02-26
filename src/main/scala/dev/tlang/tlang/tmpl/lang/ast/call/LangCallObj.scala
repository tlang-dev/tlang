package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class LangCallObj(context: Null, var props: Option[LangProp] = None, var firstCall: LangCallObjType[_], var calls: List[LangCallObjectLink]) extends LangSimpleValueType[LangCallObj] with LangExpression[LangCallObj] {
//  override def deepCopy(): LangCallObj = LangCallObj(context,
//    if (props.isDefined) Some(props.get.deepCopy()) else None,
//    firstCall.deepCopy().asInstanceOf[LangCallObjType[_]],
//    calls.map(_.deepCopy()))


  override def getElement: LangCallObj = this

  override def getType: Type = LangCallObj.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallObj.modelName)),
    Some(List(
//      BuildLang.createAttrNull(context, "props",
//        if (props.isDefined) Some(props.get.toEntity) else None,
//        None
//      ),
      BuildLang.createAttrEntity(context, "firstCall", firstCall.toEntity),
//      BuildLang.createArray(context, "calls", calls.map(_.toEntity))
    ))
  )

  override def getContext: Null = context
}

object LangCallObj {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("props"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("firstCall"), ModelSetType(Null.empty(), LangCallObjType.modelName)),
    ModelSetAttribute(Null.empty(), Some("calls"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
