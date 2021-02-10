package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast._

import scala.jdk.CollectionConverters._

object BuildAst {

  def build(domain: DomainModelContext): DomainModel = {
    DomainModel(if (domain.header != null) Some(buildHeader(domain.header)) else None,
      buildBody(domain.body.asScala.toList))
  }

  def buildHeader(header: DomainHeaderContext): DomainHeader = {
    DomainHeader(
      if (header.exposes != null && !header.exposes.isEmpty) Some(header.exposes.asScala.toList.map(buildExpose)) else None,
      if (header.uses != null && !header.uses.isEmpty) Some(header.uses.asScala.toList.map(buildUse)) else None)
  }

  def buildBody(bodies: List[DomainBlockContext]): List[DomainBlock] = {
    bodies.map {
      case body@_ if body.modelBlock() != null => BuildModelBlock.build(body.modelBlock())
      case body@_ if body.helperBlock() != null => BuildHelperBlock.build(body.helperBlock())
      case body@_ if body.tmplBlock() != null => BuildTmplBlock.build(body.tmplBlock())
    }
  }

  def buildExpose(expose: DomainExposeContext): DomainExpose = {
    DomainExpose(expose.expose.getText)
  }

  def buildUse(use: DomainUseContext): DomainUse = {
    DomainUse(use.uses.asScala.toList.map(_.getText))
  }

}
