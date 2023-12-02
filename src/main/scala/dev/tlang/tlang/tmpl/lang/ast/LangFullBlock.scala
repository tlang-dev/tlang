package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.createArray

import scala.collection.mutable.ListBuffer

case class LangFullBlock(context: Option[ContextContent], name: String, lang: String,
                         var params: Option[List[HelperParam]],
                         var pkg: Option[TmplPkg] = None,
                         var uses: Option[List[TmplUse]] = None,
                         var specialised: Boolean = false,
                         var content: Option[List[TmplNode[_]]] = None,
                         scope: Scope = Scope()) extends DomainBlock with DeepCopy with TmplNode[LangFullBlock] {

  override def deepCopy(): LangFullBlock =
    LangFullBlock(context, name, lang, params,
      if (pkg.isDefined) Some(pkg.get.deepCopy()) else None,
      if (uses.isDefined) Some(uses.get.map(_.deepCopy())) else None,
      specialised,
      if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
      scope)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangFullBlock]): Int = 0

  override def getElement: LangFullBlock = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = {
    val elems = ListBuffer.empty[ComplexAttribute]

    if (pkg.nonEmpty) elems += ComplexAttribute(context, Some("package"),
      Some(ObjType(context, None, TmplLangAst.langPkg.name)), Operation(context, None, Right(pkg.get.toEntity)))

    elems += createArray(context, "uses", if (uses.isDefined) uses.get.map(_.toEntity) else List())
    elems += createArray(context, "contents", if (content.isDefined) content.get.map(_.toEntity) else List())

    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langFullBlock.name)),
      Some(elems.toList))
  }

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
