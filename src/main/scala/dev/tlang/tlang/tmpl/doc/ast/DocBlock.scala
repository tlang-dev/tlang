package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue, TLangString}
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, TmplStringId}

case class DocBlock(context: Null[ContextContent], name: String, langs: List[String],
                    var params: Option[List[NativeType[HelperParam]]], content: DocContent, scope: Scope = Scope()) extends DomainBlock with AnyTmplInterpretedBlock[DocBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "name", name),
      BuildLang.createArray(context, "langs", langs.map(value => new TmplStringId(context, new core.String(value)).toEntity)),
      BuildLang.createAttrEntity(context, "content", content.toEntity)
    ))
  )

  override def toModel: ModelSetEntity = DocBlock.model

  override def compareTo(value: Value[DocBlock]): Int = 0

  override def getElement: DocBlock = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Null[ContextContent] = context

  override def deepCopy(): Any = DocBlock(context, new String(name), langs.map(new String(_)), params, content.deepCopy(), scope)

  override def getParams: Option[List[HelperParam]] = params.map(_.map(_.getElement))

  override def getLangs: List[String] = langs

  override def getScope: Scope = scope

  override def getName: String = name
}

object DocBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("langs"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), DocContent.name)),
  )))
}