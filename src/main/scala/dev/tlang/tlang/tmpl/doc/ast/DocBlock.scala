package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangString}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core
import tlang.core.{Array, Null, Type}
import tlang.internal.DomainBlock

case class DocBlock(context: Null, name: String, langs: Array,
                    var params: Option[List[NativeType[HelperParam]]], content: DocContent, scope: Scope = Scope()) extends DomainBlock with AnyTmplInterpretedBlock[DocBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocBlock.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "name", name),
      //      BuildLang.createArray(context, "langs", langs.map(value => new TmplStringId(context, new core.String(value)).toEntity)),
      //      BuildLang.createAttrEntity(context, "content", content.toEntity)
    ))
  )

  //  override def toModel: ModelSetEntity = DocBlock.model

  override def getType: Type = DocBlock.modelName

  override def getContext: Null = context

  //  override def deepCopy(): Any = DocBlock(context, new String(name), langs.map(new String(_)), params, content.deepCopy(), scope)

  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.statement))

  override def getLangs: Array = langs

  override def getScope: Scope = scope

  override def getName: core.String = new core.String(name)

  override def getElement: DocBlock = this
}

object DocBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)


  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("langs"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), DocContent.modelName)),
  )))
}