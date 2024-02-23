package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, TLangString}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangParam}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class LangFuncParam(context: Null[ContextContent], params: Option[List[LangParam]], var `type`: String) extends TmplNode[LangFuncParam] {

  override def getElement: LangFuncParam = this

  override def getType: Type = LangFuncParam.modelName

  override def getContext: Null[ContextContent] = context

//  override def deepCopy(): LangFuncParam = LangFuncParam(context,
//    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
//    `type`
//  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangFuncParam.modelName)),
    Some(List(
//      BuildLang.createAttrNull(context, "params",
//        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
//        None
//      ),
      BuildLang.createAttrStr(context, "tType", `type`),
    ))
  )

}

object LangFuncParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), TLangString.getType)),
  )))
}
