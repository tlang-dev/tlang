package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core
import tlang.core.Null
import tlang.internal.{ContextContent, TmplStringId}

case class StyleBlock(context: Null[ContextContent], name: String, langs: List[String],
                      var params: Option[List[NativeType[HelperParam]]], contents: List[StyleStruct], scope: Scope = Scope()) extends AnyTmplInterpretedBlock[StyleBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "name", name),
      BuildLang.createArray(context, "langs", langs.map(value => new TmplStringId(context, new core.String(value)).toEntity)),
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createArray(context, "contents", contents.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = StyleBlock.model

  override def getElement: StyleBlock = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleBlock = StyleBlock(context, new String(name), langs.map(new String(_)), params, contents.map(_.deepCopy()), scope)

  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.getElement))

  override def getLangs: List[String] = langs

  override def getScope: Scope = scope

  override def getName: String = getClass.getSimpleName

}

object StyleBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, StyleModel.styleModel.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("langs"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("contents"), ModelSetType(Null.empty(), ArrayValue.getType)),
  )))
}
