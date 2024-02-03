package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangImpl(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: TmplID, var fors: Option[LangImplFor], var withs: Option[LangImplWith], var content: Option[List[TmplNode[_]]] = None) extends LangContent[LangImpl] with AstContext {
  override def deepCopy(): LangImpl = LangImpl(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (fors.isDefined) Some(fors.get.deepCopy()) else None,
    if (withs.isDefined) Some(withs.get.deepCopy()) else None,
    if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[LangContent[_]])) else None
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangImpl]): Int = 0

  override def getElement: LangImpl = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangImpl.name)),
    Some(List(
      BuildLang.createAttrNull(context, "annots",
        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrNull(context, "props",
        if (props.isDefined) Some(props.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "fors",
        if (fors.isDefined) Some(fors.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrNull(context, "withs",
        if (withs.isDefined) Some(withs.get.toEntity) else None,
        None
      ),
      //      BuildLang.createAttrEntity(context, "fors", fors.t),
      BuildLang.createAttrNull(context, "content",
        if (content.isDefined) Some(ArrayValue(context, Some(content.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
    ))
  )

  override def toModel: ModelSetEntity = LangImpl.model
}

object LangImpl {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("annots"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("props"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TmplID.name)),
    ModelSetAttribute(None, Some("fors"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("withs"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("content"), ModelSetType(None, NullValue.name)),
  )))
}