package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent, TmplID}

case class LangVal(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: TmplID, var `type`: Option[LangType], var value: Option[LangOperation], isOptional: Boolean) extends LangExpression[LangVal] with Context {
  //  override def deepCopy(): LangVal = LangVal(context,
  //    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
  //    if (props.isDefined) Some(props.get.deepCopy()) else None,
  //    name.deepCopy().asInstanceOf[TmplID],
  //    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
  //    if (value.isDefined) Some(value.get.deepCopy()) else None,
  //    isOptional
  //  )

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangVal.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "annots",
      //        if (annots.isDefined) Null.of(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "props",
      //        if (props.isDefined) Null.of(props.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrNull(context, "tType",
      //        if (`type`.isDefined) Null.of(`type`.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "value",
      //        if (value.isDefined) Null.of(value.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildAstTmpl.createAttrBool(context, Some("isOptional"), new TLangBool(None, isOptional)),
    ))
  )

  override def getElement: LangVal = this

  override def getType: Type = LangVal.modelName
}

object LangVal {
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