package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core
import tlang.core.{Array, Null, Type}
import tlang.internal.{AnyTmplBlock, ContextContent}

case class LangBlock(context: Null[ContextContent], name: String, langs: Array[core.String],
                     var params: Option[List[NativeType[HelperParam]]],
                     var content: LangFullBlock,
                     scope: Scope = Scope()) extends AnyTmplBlock[LangBlock] {

  //  override def deepCopy(): LangBlock =
  //    LangBlock(context, name, langs, params,
  //      content.deepCopy(),
  //      scope)

  override def getElement: LangBlock = this

  override def getType: Type = LangBlock.modelName

  override def toEntity: EntityValue = {
    EntityValue(context, Some(ObjType(context, None, LangBlock.modelName)), Some(List(
      BuildLang.createAttrStr(context, "name", name),
      //      BuildLang.createArray(context, "langs", langs.map(value => new TmplStringId(context, new core.String(value.getElement)).toEntity)),
      //      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      ComplexAttribute(context, Some("content"),
        Some(ObjType(context, None, LangFullBlock.modelName)), Operation(context, None, Right(content.toEntity))
      )
    )))
  }

  //  override def toModel: ModelSetEntity = LangBlock.model

  //  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.getElement))

  override def getLangs: Array[core.String] = langs

  //  override def getScope: Scope = scope

  override def getName: core.String = new core.String(name)

  override def getContext: Null[ContextContent] = context
}

object LangBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("langs"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), LangFullBlock.modelName)),
  )))
}
