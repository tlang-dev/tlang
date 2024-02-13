package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallFuncParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangType(context: Null[ContextContent], var name: TmplID, var generic: Option[LangGeneric] = None, isArray: Boolean = false, var currying: Option[List[LangCallFuncParam]] = None) extends TmplNode[LangType] {
  override def deepCopy(): LangType = LangType(context, name.deepCopy().asInstanceOf[TmplID],
    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
    if (isArray) true else false,
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None,
  )

  override def compareTo(value: Value[LangType]): Int = 0

  override def getElement: LangType = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangType.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "generic",
        if (generic.isDefined) Null.of(generic.get.toEntity) else Null.empty(),
        None
      ),
    ))
  )

  override def toModel: ModelSetEntity = LangType.model
}

object LangType {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.name)),
    ModelSetAttribute(Null.empty(), Some("generic"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}
