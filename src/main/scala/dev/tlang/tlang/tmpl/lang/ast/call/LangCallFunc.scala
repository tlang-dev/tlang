package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangCallFunc(context: Null[ContextContent], var name: TmplID, var currying: Option[List[LangCallFuncParam]]) extends LangCallObjType[LangCallFunc] {
  override def deepCopy(): LangCallFunc = LangCallFunc(context, name.deepCopy().asInstanceOf[TmplID],
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None)


  override def compareTo(value: Value[LangCallFunc]): Int = 0

  override def getElement: LangCallFunc = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallFunc.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "currying",
        if (currying.isDefined) Some(ArrayValue(context, Some(currying.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = LangCallFunc.model
}

object LangCallFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, "LangCallFunc", Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.name)),
    ModelSetAttribute(Null.empty(), Some("currying"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}
