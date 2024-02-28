package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangCallObj(context: Option[ContextContent], var props: Option[LangProp] = None, var firstCall: LangCallObjType[_], var calls: List[LangCallObjectLink]) extends LangSimpleValueType[LangCallObj] with LangExpression[LangCallObj] {
  //  override def deepCopy(): LangCallObj = LangCallObj(context,
  //    if (props.isDefined) Some(props.get.deepCopy()) else None,
  //    firstCall.deepCopy().asInstanceOf[LangCallObjType[_]],
  //    calls.map(_.deepCopy()))


  override def getElement: LangCallObj = this

  override def getType: Type = LangCallObj.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangCallObj.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "props",
      //        if (props.isDefined) Some(props.get.toEntity) else None,
      //        None
      //      ),
//      BuildAstTmpl.createAttrEntity(context, "firstCall", firstCall.toEntity),
      //      BuildLang.createArray(context, "calls", calls.map(_.toEntity))
    ))
  )

  override def getContext: Option[ContextContent] = context
}

object LangCallObj {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("props")),
//    BuildAstTmpl.createModelAttrEntity(None, Some("firstCall"), LangCallObjType.model),
    BuildAstTmpl.createModelAttrArray(None, Some("calls")),
  )))
}
