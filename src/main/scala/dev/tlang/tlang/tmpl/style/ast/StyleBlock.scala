package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.{AnyTmplInterpretedBlock, AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class StyleBlock(context: Option[ContextContent], name: String, langs: List[String],
                      var params: Option[List[NativeType[HelperParam]]], contents: List[StyleStruct], scope: Scope = Scope()) extends AnyTmplInterpretedBlock[StyleBlock] {
  override def toEntity: AstEntity = AstEntity(context,
    Some(StyleBlock.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "name", name),
      //      BuildLang.createArray(context, "langs", langs.map(value => new TmplStringId(context, new core.String(value.getElement.toString)).toEntity).get().get().getElement),
      //      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildLang.createArray(context, "contents", contents.map(_.toEntity))
    ))
  )

  override def getElement: StyleBlock = this

  override def getType: Type = StyleBlock.modelName

  //  override def deepCopy(): StyleBlock = StyleBlock(context, new String(name), langs.map(new String(_)), params, contents.map(_.deepCopy()), scope)

  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.getElement.asInstanceOf[HelperParam]))


  override def getScope: Scope = scope


  override def getContext: Option[ContextContent] = context

  override def getLangs: List[String] = langs

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = StyleBlock.model
}

object StyleBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(StyleModel.styleModel), None, Some(List(
    BuildAstTmpl.createModelAttrStr(None, Some("name")),
    BuildAstTmpl.createModelAttrStr(None, Some("langs")),
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
    BuildAstTmpl.createModelAttrArray(None, Some("contents")),
  )))
}
