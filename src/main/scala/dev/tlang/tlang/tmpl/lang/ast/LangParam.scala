package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangParam(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var name: TmplID, var `type`: Option[LangType]) extends TmplNode[LangParam] {
  override def deepCopy(): LangParam = LangParam(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangParam]): Int = 0

  override def getElement: LangParam = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangParam.name)),
    Some(List(
      BuildLang.createAttrNull(context, "annots",
        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "tType",
        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = LangParam.model
}

object LangParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("annots"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TmplID.name)),
    ModelSetAttribute(None, Some("tType"), ModelSetType(None, NullValue.name)),
  )))
}