package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.call.LangCallFuncParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangType(context: Null[ContextContent], var name: TmplID, var generic: Option[LangGeneric] = None, isArray: Boolean = false, var currying: Option[List[LangCallFuncParam]] = None) extends TmplNode[LangType] {
  override def deepCopy(): LangType = LangType(context, name.deepCopy().asInstanceOf[TmplID],
    if (generic.isDefined) Some(generic.get.deepCopy()) else None,
    if (isArray) true else false,
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None,
  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangType.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "generic",
        generic,
        None
      ),
    ))
  )

  override def toModel: ModelSetEntity = LangType.model

  override def getContext: Null[ContextContent] = context
}

object LangType {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.TYPE)),
    ModelSetAttribute(Null.empty(), Some("generic"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
