package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.tmpl._
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import tlang.core.Type
import tlang.internal.{ContextContent, DomainBlock}

import scala.collection.mutable.ListBuffer

case class LangFullBlock(context: Option[ContextContent],
                         var pkg: Option[LangPkg] = None,
                         var uses: Option[List[LangUse]] = None,
                         var specialised: Boolean = false,
                         var content: Option[List[AstTmplNode]] = None,
                         scope: Scope = Scope()) extends DomainBlock with AstTmplNode {

  //  override def deepCopy(): LangFullBlock =
  //    LangFullBlock(context,
  //      if (pkg.isDefined) Some(pkg.get.deepCopy()) else None,
  //      if (uses.isDefined) Some(uses.get.map(_.deepCopy())) else None,
  //      specialised,
  //      if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[AstTmplNode])) else None,
  //      scope)

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = {
    val elems = ListBuffer.empty[AstEntityAttr]

    //    if (pkg.nonEmpty) elems += ComplexAttribute(context, Some("tpkg"),
    //      Some(LangPkg.model), Operation(context, None, Right(pkg.get.toEntity)))
    //
    ////    elems += createArray(context, "uses", if (uses.isDefined) uses.get.map(_.toEntity) else List())
    //    elems += createAttrBool(context, "specialized", specialised)
    //    elems += createArray(context, "contents", if (content.isDefined) content.get.map(_.toEntity) else List())

    AstEntity(context,
      Some(LangFullBlock.model),
      Some(elems.toList))
  }

  //  override def toModel: ModelSetEntity = LangFullBlock.model

  override def getElement: LangFullBlock = this

  override def getType: Type = LangFullBlock.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangFullBlock.model
}

object LangFullBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("tPkg")),
    BuildAstTmpl.createModelAttrNull(None, Some("uses")),
    BuildAstTmpl.createModelAttrBool(None, Some("specialized")),
    BuildAstTmpl.createModelAttrNull(None, Some("contents")),
  )))
}
