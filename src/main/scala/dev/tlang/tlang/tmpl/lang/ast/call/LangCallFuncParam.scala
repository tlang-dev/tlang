package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class LangCallFuncParam(context: Null[ContextContent], var params: Option[List[TmplNode[_]]]) extends TmplNode[LangCallFuncParam] {

  override def getType: Type = LangCallFuncParam.modelName

  override def getContext: Null[ContextContent] = context

//  override def deepCopy(): LangCallFuncParam = LangCallFuncParam(
//    context,
//    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
//  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallFuncParam.modelName)),
    Some(List(
//      BuildLang.createAttrNull(context, "params",
//        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
//        None
//      )
    ))
  )

  override def getElement: LangCallFuncParam = this
}

object LangCallFuncParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
