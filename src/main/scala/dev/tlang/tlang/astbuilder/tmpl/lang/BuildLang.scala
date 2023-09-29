package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.TLangParser
import dev.tlang.tlang.TLangParser.{TmplFullBlockContext, TmplLangContext}
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, TLangString}
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala

object BuildLang {

  def buildLangBlock(resource: ContextResource, block: TmplLangContext): EntityValue = {
    val context = addContext(resource, block)
    EntityValue(context, Some(ObjType(context, None, TmplLangAst.tmplLang.name)), Some(List(
      ComplexAttribute(context, Some("content"),
        Some(ObjType(context, None, TmplLangAst.langFullBlock.name)), Operation(context, None, Right(buildFullBlock(resource, block.tmplFullBlock()))))
    )))
  }

  def buildFullBlock(resource: ContextResource, full: TmplFullBlockContext): EntityValue = {
    val elems = ListBuffer.empty[ComplexAttribute]
    val context = addContext(resource, full)

    if (full.tmplPkg() != null && !full.tmplPkg().isEmpty) elems += ComplexAttribute(context, Some("package"),
      Some(ObjType(context, None, TmplLangAst.langFullBlock.name)), Operation(context, None, Right(buildPkg(resource, full.tmplPkg()))))

    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langFullBlock.name)),
      Some(elems.toList))
  }

  def buildPkg(resource: ContextResource, pkg: TLangParser.TmplPkgContext): EntityValue = {
    val context = addContext(resource, pkg)
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langPkg.name)),
      Some(List(
        ComplexAttribute(context, Some("parts"),
          None, Operation(context, None, Right(ArrayValue(context, Some(pkg.parts.asScala.toList.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.getText)))))))))
        ))
      ))
  }

}
