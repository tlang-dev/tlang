package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import tlang.internal.TmplNode
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type, Value}
import tlang.internal.{AstContext, ContextContent}

case class LangImplWith(context: Null[ContextContent], var props: Option[LangProp] = None, var types: List[LangType]) extends TmplNode[LangImplWith] with AstContext {
  override def deepCopy(): LangImplWith = LangImplWith(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    types.map(_.deepCopy()))

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangImplWith.modelName)),
    Some(List(
      BuildLang.createAttrNull(context, "props",
        props,
        None
      ),
      BuildLang.createArray(context, "types", types.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = LangImplWith.model

  override def getContext: Null[ContextContent] = context

}

object LangImplWith {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("props"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("types"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
