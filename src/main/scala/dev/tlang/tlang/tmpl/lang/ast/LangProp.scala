package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal._

case class LangProp(context: Null[ContextContent], var props: List[TmplID]) extends AstContext with TmplNode[LangProp] {
  //  override def deepCopy(): LangProp = LangProp(context, props.map(_.deepCopy().asInstanceOf[TmplID]))

  override def getContext: Null[ContextContent] = context

  override def getElement: LangProp = this

  override def getType: Type = LangProp.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangProp.modelName)),
    Some(List(
      BuildLang.createArray(context, "props", props.map(_.toEntity))
    ))
  )

}

object LangProp {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("props"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
