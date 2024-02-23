package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID}

case class LangCallFunc(context: Null[ContextContent], var name: TmplID, var currying: Option[List[LangCallFuncParam]]) extends LangCallObjType[LangCallFunc] {
//  override def deepCopy(): LangCallFunc = LangCallFunc(context, name.deepCopy().asInstanceOf[TmplID],
//    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None)


  override def getElement: LangCallFunc = this

  override def getType: Type = LangCallFunc.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallFunc.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrNull(context, "currying",
      //        if (currying.isDefined) Some(ArrayValue(context, Some(currying.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      )
    ))
  )

  override def getContext: Null[ContextContent] = context
}

object LangCallFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.TYPE)),
    ModelSetAttribute(Null.empty(), Some("currying"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
