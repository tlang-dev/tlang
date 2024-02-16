package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import tlang.internal.TmplNode
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplID}

case class LangImpl(context: Null[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: TmplID, var fors: Option[LangImplFor], var withs: Option[LangImplWith], var content: Option[List[TmplNode[_]]] = None) extends LangContent[LangImpl] with AstContext {
  override def deepCopy(): LangImpl = LangImpl(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (fors.isDefined) Some(fors.get.deepCopy()) else None,
    if (withs.isDefined) Some(withs.get.deepCopy()) else None,
    if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[LangContent[_]])) else None
  )

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

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("annots"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("props"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.name)),
    ModelSetAttribute(Null.empty(), Some("fors"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("withs"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}