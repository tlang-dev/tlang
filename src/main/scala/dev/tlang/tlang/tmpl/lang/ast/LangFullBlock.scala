package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue, NullValue, TLangBool}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.{createArray, createAttrBool}
import dev.tlang.tlang.tmpl.{DeepCopy, TmplNode}

import scala.collection.mutable.ListBuffer

case class LangFullBlock(context: Option[ContextContent],
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

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangFullBlock]): Int = 0

  override def getElement: LangFullBlock = this

  override def getType: String = getClass.getSimpleName

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

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("tPkg"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("uses"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("specialized"), ModelSetType(None, TLangBool.getType)),
    ModelSetAttribute(None, Some("contents"), ModelSetType(None, NullValue.name)),
  )))
}
