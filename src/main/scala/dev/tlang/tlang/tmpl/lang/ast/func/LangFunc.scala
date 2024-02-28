package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{Context, ContextContent, TmplID}

case class LangFunc(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var preNames: Option[List[TmplID]] = None, var name: TmplID, var curries: Option[List[LangFuncParam]], var content: Option[LangExprContent[_]],
                    var ret: Option[List[LangType]] = None, postPros: Option[LangProp] = None) extends LangExpression[LangFunc] with LangContent[LangFunc] with Context {
  //  override def deepCopy(): LangFunc = LangFunc(context,
  //    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
  //    if (props.isDefined) Some(props.get.deepCopy()) else None,
  //    if (preNames.isDefined) Some(preNames.get.map(_.deepCopy().asInstanceOf[TmplID])) else None,
  //    name.deepCopy().asInstanceOf[TmplID],
  //    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
  //    if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[LangExprContent[_]]) else None,
  //    if (ret.isDefined) Some(ret.get.map(_.deepCopy())) else None,
  //    if (postPros.isDefined) Some(postPros.get.deepCopy()) else None)


  override def getElement: LangFunc = this

  override def getType: Type = LangFunc.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangFunc.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "annots",
      //        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "props",
      //        if (props.isDefined) Some(props.get.toEntity) else None,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "preNames",
      //        if (preNames.isDefined) Some(ArrayValue(context, Some(preNames.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildAstTmpl.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrNull(context, "curries",
      //        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildLang.createAttrEntity(context, "content", content.map(_.toEntity).getOrElse(EntityValue(context, None, None))),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangFunc.model
}

object LangFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("annots")),
    BuildAstTmpl.createModelAttrNull(None, Some("props")),
    BuildAstTmpl.createModelAttrNull(None, Some("preNames")),
    BuildAstTmpl.createModelAttrTmplID(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("curries")),
    BuildAstTmpl.createModelAttrNull(None, Some("content")),
  )))
}
