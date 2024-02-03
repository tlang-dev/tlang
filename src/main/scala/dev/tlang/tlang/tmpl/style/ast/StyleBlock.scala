package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplBlock
import dev.tlang.tlang.tmpl.common.ast.{NativeType, TmplStringID}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class StyleBlock(context: Option[ContextContent], name: String, langs: List[String],
                      var params: Option[List[NativeType[HelperParam]]], content: StyleStruct, scope: Scope = Scope()) extends AnyTmplBlock[StyleBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "name", name),
      BuildLang.createArray(context, "langs", langs.map(value => TmplStringID(context, value).toEntity)),
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      ComplexAttribute(context, Some("content"),
        Some(ObjType(context, None, StyleStruct.name)), Operation(context, None, Right(content.toEntity))
      )
    ))
  )

  override def toModel: ModelSetEntity = StyleBlock.model

  override def compareTo(value: Value[StyleBlock]): Int = 0

  override def getElement: StyleBlock = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): StyleBlock = StyleBlock(context, new String(name), langs.map(new String(_)), params, content.deepCopy(), scope)

  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.getElement))

  override def getLangs: List[String] = langs

  override def getScope: Scope = scope

  override def getName: String = getClass.getSimpleName
}

object StyleBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TLangString.getType)),
    ModelSetAttribute(None, Some("langs"), ModelSetType(None, TLangString.getType)),
    ModelSetAttribute(None, Some("params"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("content"), ModelSetType(None, StyleStruct.name)),
  )))
}
