package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue, NullValue, TLangBool}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.{createArray, createAttrBool}
import tlang.core.Null
import tlang.internal.{ContextContent, DeepCopy, TmplNode}

import scala.collection.mutable.ListBuffer

case class LangFullBlock(context: Null[ContextContent],
                         var pkg: Option[LangPkg] = None,
                         var uses: Option[List[LangUse]] = None,
                         var specialised: Boolean = false,
                         var content: Option[List[TmplNode[_]]] = None,
                         scope: Scope = Scope()) extends DomainBlock with DeepCopy with TmplNode[LangFullBlock] {

  override def deepCopy(): LangFullBlock =
    LangFullBlock(context,
      if (pkg.isDefined) Some(pkg.get.deepCopy()) else None,
      if (uses.isDefined) Some(uses.get.map(_.deepCopy())) else None,
      specialised,
      if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
      scope)

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = {
    val elems = ListBuffer.empty[ComplexAttribute]

    if (pkg.nonEmpty) elems += ComplexAttribute(context, Some("tpkg"),
      Some(ObjType(context, None, LangPkg.name)), Operation(context, None, Right(pkg.get.toEntity)))

    elems += createArray(context, "uses", if (uses.isDefined) uses.get.map(_.toEntity) else List())
    elems += createAttrBool(context, "specialized", specialised)
    elems += createArray(context, "contents", if (content.isDefined) content.get.map(_.toEntity) else List())

    EntityValue(context,
      Some(ObjType(context, None, LangFullBlock.name)),
      Some(elems.toList))
  }

  override def toModel: ModelSetEntity = LangFullBlock.model
}

object LangFullBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("tPkg"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("uses"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("specialized"), ModelSetType(Null.empty(), TLangBool.getType)),
    ModelSetAttribute(Null.empty(), Some("contents"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}
