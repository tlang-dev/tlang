package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangType}
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class LangArrayValue(context: Null, var `type`: Option[LangType] = None, var params: Option[List[TmplNode[_]]]) extends LangPrimitiveValue[LangArrayValue] {
//  override def deepCopy(): LangArrayValue = LangArrayValue(context,
//    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
//    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None)



  override def getElement: LangArrayValue = this

  override def getType: Type = LangArrayValue.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangArrayValue.modelName)),
    Some(List(
      //      BuildLang.createAttrNull(context, "tType",
      //        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
      //        None
      //      ),
//      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      )
    ))
  )


  override def getContext: Null = context
}

object LangArrayValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
