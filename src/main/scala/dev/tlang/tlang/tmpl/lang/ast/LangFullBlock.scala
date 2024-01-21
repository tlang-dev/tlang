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
                         var pkg: Option[LangPkg] = None,
                         var uses: Option[List[LangUse]] = None,
                         var specialised: Boolean = false,
                         var content: Option[List[LangNode[_]]] = None,
                         scope: Scope = Scope()) extends DomainBlock with DeepCopy with LangNode[LangFullBlock] {

  override def deepCopy(): LangFullBlock =
    LangFullBlock(context, name, lang, params,
      if (pkg.isDefined) Some(pkg.get.deepCopy()) else None,
      if (uses.isDefined) Some(uses.get.map(_.deepCopy())) else None,
      specialised,
      if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[LangNode[_]])) else None,
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
  )))
}
