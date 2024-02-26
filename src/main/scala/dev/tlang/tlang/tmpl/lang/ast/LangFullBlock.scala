package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.{createArray, createAttrBool}
import tlang.core.{Bool, Null, Type}
import tlang.internal.{ContextContent, DeepCopy, DomainBlock, TmplNode}

import scala.collection.mutable.ListBuffer

case class LangFullBlock(context: Null,
                         var pkg: Option[LangPkg] = None,
                         var uses: Option[List[LangUse]] = None,
                         var specialised: Boolean = false,
                         var content: Option[List[TmplNode[_]]] = None,
                         scope: Scope = Scope()) extends DomainBlock with TmplNode[LangFullBlock] {

//  override def deepCopy(): LangFullBlock =
//    LangFullBlock(context,
//      if (pkg.isDefined) Some(pkg.get.deepCopy()) else None,
//      if (uses.isDefined) Some(uses.get.map(_.deepCopy())) else None,
//      specialised,
//      if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
//      scope)

  override def getContext: Null = context

  override def toEntity: EntityValue = {
    val elems = ListBuffer.empty[ComplexAttribute]

    if (pkg.nonEmpty) elems += ComplexAttribute(context, Some("tpkg"),
      Some(ObjType(context, None, LangPkg.modelName)), Operation(context, None, Right(pkg.get.toEntity)))

//    elems += createArray(context, "uses", if (uses.isDefined) uses.get.map(_.toEntity) else List())
    elems += createAttrBool(context, "specialized", specialised)
    elems += createArray(context, "contents", if (content.isDefined) content.get.map(_.toEntity) else List())

    EntityValue(context,
      Some(ObjType(context, None, LangFullBlock.modelName)),
      Some(elems.toList))
  }

//  override def toModel: ModelSetEntity = LangFullBlock.model

  override def getElement: LangFullBlock = this

  override def getType: Type = LangFullBlock.modelName
}

object LangFullBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("tPkg"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("uses"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("specialized"), ModelSetType(Null.empty(), Bool.TYPE)),
    ModelSetAttribute(Null.empty(), Some("contents"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
