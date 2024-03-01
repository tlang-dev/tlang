package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangParam(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var name: TmplID, var `type`: Option[LangType]) extends AstTmplNode {
  //  override def deepCopy(): LangParam = LangParam(context,
  //    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
  //    name.deepCopy().asInstanceOf[TmplID],
  //    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None)


  override def toEntity: AstEntity = AstEntity(context,
    Some(LangParam.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "annots",
      //        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildAstTmpl.createAttrEntity(context, "name", Some(TmplID.TYPE), name.toEntity),
      //      BuildLang.createAttrNull(context, "tType",
      //        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
      //        None
      //      )
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getElement: LangParam = this

  override def getType: Type = LangParam.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangParam.model
}

object LangParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("annots")),
    BuildAstTmpl.createModelAttrNull(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("tType")),
  )))
}