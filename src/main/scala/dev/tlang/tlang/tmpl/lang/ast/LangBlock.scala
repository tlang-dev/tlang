package dev.tlang.tlang.tmpl.lang.ast

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
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplStringId}

case class LangBlock(context: Null[ContextContent], name: String, langs: List[String],
                     var params: Option[List[NativeType[HelperParam]]],
                     var content: LangFullBlock,
                     scope: Scope = Scope()) extends AnyTmplInterpretedBlock[LangBlock] {

  override def deepCopy(): LangBlock =
    LangBlock(context, name, langs, params,
      content.deepCopy(),
      scope)

  override def compareTo(value: Value[LangBlock]): Int = 0

  override def getElement: LangBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = {
    EntityValue(context, Some(ObjType(context, None, LangBlock.name)), Some(List(
      BuildLang.createAttrStr(context, "name", name),
      BuildLang.createArray(context, "langs", langs.map(value => new TmplStringId(context, new core.String(value)).toEntity)),
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      ComplexAttribute(context, Some("content"),
        Some(ObjType(context, None, LangFullBlock.name)), Operation(context, None, Right(content.toEntity))
      )
    )))
  }

  override def toModel: ModelSetEntity = LangBlock.model

  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.getElement))

  override def getLangs: List[String] = langs

  override def getScope: Scope = scope

  override def getName: String = name
}

object LangBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "LangBlock", Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("langs"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), LangFullBlock.name)),
  )))
}
