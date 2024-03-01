package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl, TmplID}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangVar(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: TmplID, var `type`: Option[LangType], var value: Option[LangOperation], isOptional: Boolean) extends LangExpression[LangVar] {
  //  override def deepCopy(): LangVar = LangVar(context,
  //    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
  //    if (props.isDefined) Some(props.get.deepCopy()) else None,
  //    name.deepCopy().asInstanceOf[TmplID],
  //    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
  //    if (value.isDefined) Some(value.get.deepCopy()) else None,
  //    isOptional
  //  )

  override def getContext: Option[ContextContent] = context

  override def getElement: LangVar = this

  override def getType: Type = LangVar.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangVar.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "annots",
      //        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "props",
      //        if (props.isDefined) Null.of(props.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildAstTmpl.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrNull(context, "tType",
      //        if (`type`.isDefined) Null.of(`type`.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "value",
      //        if (value.isDefined) Null.of(value.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildAstTmpl.createAttrBool(context, "isOptional", isOptional),
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangVar.model
}

object LangVar {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("annots")),
    BuildAstTmpl.createModelAttrNull(None, Some("props")),
    BuildAstTmpl.createModelAttrTmplID(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("tType")),
    BuildAstTmpl.createModelAttrNull(None, Some("value")),
    BuildAstTmpl.createModelAttrBool(None, Some("isOptional")),
  )))
}
