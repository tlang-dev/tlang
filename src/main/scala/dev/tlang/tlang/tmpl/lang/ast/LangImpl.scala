package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent, TmplID, TmplNode}

case class LangImpl(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: TmplID, var fors: Option[LangImplFor], var withs: Option[LangImplWith], var content: Option[List[TmplNode[_]]] = None) extends LangContent[LangImpl] with Context {
  //  override def deepCopy(): LangImpl = LangImpl(context,
  //    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
  //    if (props.isDefined) Some(props.get.deepCopy()) else None,
  //    name.deepCopy().asInstanceOf[TmplID],
  //    if (fors.isDefined) Some(fors.get.deepCopy()) else None,
  //    if (withs.isDefined) Some(withs.get.deepCopy()) else None,
  //    if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[LangContent[_]])) else None
  //  )

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangImpl.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "annots",
      //        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "props",
      //        if (props.isDefined) Some(props.get.toEntity) else None,
      //        None
      //      ),
//      BuildAstTmpl.createAttrEntity(context, "name", Some(TmplID.TYPE), name.toEntity),
      //      BuildLang.createAttrNull(context, "fors",
      //        if (fors.isDefined) Null.of(fors.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "withs",
      //        if (withs.isDefined) Null.of(withs.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrEntity(context, "fors", fors.t),
      //      BuildLang.createAttrNull(context, "content",
      //        if (content.isDefined) Some(ArrayValue(context, Some(content.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
    ))
  )

  //  override def toModel: ModelSetEntity = LangImpl.model

  override def getContext: Option[ContextContent] = context

  override def getElement: LangImpl = this

  override def getType: Type = LangImpl.modelName
}

object LangImpl {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("annots")),
    BuildAstTmpl.createModelAttrNull(None, Some("props")),
    BuildAstTmpl.createModelAttrNull(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("fors")),
    BuildAstTmpl.createModelAttrNull(None, Some("withs")),
    BuildAstTmpl.createModelAttrNull(None, Some("content")),
  )))
}