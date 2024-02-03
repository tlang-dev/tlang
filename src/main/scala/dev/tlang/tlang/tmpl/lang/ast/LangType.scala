package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallFuncParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangType(context: Option[ContextContent], var name: TmplID, var generic: Option[LangGeneric] = None, isArray: Boolean = false, var currying: Option[List[LangCallFuncParam]] = None) extends TmplNode[LangType] {
  override def deepCopy(): LangType = LangType(context, name.deepCopy().asInstanceOf[TmplID],
    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
    if (isArray) true else false,
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None,
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangType]): Int = 0

  override def getElement: LangType = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangType.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "generic",
        if (generic.isDefined) Some(generic.get.toEntity) else None,
        None
      ),
    ))
  )

  override def toModel: ModelSetEntity = LangType.model
}

object LangType {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TmplID.name)),
    ModelSetAttribute(None, Some("generic"), ModelSetType(None, NullValue.name)),
  )))
}
