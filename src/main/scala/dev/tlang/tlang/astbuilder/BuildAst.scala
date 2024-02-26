package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildTmplBlock
import org.antlr.v4.runtime.ParserRuleContext
import tlang.core.{Int, Null}
import tlang.internal
import tlang.internal.{ContextContent, ContextResource}

import scala.jdk.CollectionConverters._

object BuildAst {

  def build(resource: ContextResource, domain: DomainModelContext): DomainModel = {
    DomainModel(addContext(resource, domain), if (domain.header != null) Some(buildHeader(resource, domain.header)) else None,
      buildBody(resource, domain.body.asScala.toList))
  }

  def buildHeader(resource: ContextResource, header: DomainHeaderContext): DomainHeader = {
    DomainHeader(addContext(resource, header),
      if (header.exposes != null && !header.exposes.isEmpty) Some(header.exposes.asScala.toList.map(expose => buildExpose(resource, expose))) else None,
      if (header.uses != null && !header.uses.isEmpty) Some(header.uses.asScala.toList.map(use => buildUse(resource, use))) else None)
  }

  def buildBody(resource: ContextResource, bodies: List[DomainBlockContext]): List[internal.DomainBlock] = {
    bodies.map {
      case body@_ if body.modelBlock() != null => BuildModelBlock.build(resource, body.modelBlock())
      case body@_ if body.helperBlock() != null => BuildHelperBlock.build(resource, body.helperBlock())
      case body@_ if body.tmplBlock() != null => BuildTmplBlock.build(resource, body.tmplBlock())
    }
  }

  def buildExpose(resource: ContextResource, expose: DomainExposeContext): DomainExpose = {
    DomainExpose(addContext(resource, expose), expose.expose.getText)
  }

  def buildUse(resource: ContextResource, use: DomainUseContext): DomainUse = {
    DomainUse(addContext(resource, use), use.uses.asScala.toList.map(_.getText), AstBuilderUtils.getText(use.alias))
  }

  def addContext(resource: ContextResource, parser: ParserRuleContext): Null = {
    Null.of(new ContextContent(resource, new Int(parser.getStart.getLine), new Int(parser.getStart.getCharPositionInLine)))
  }

}
