package dev.tlang.tlang.tmpl.doc.astbuilder

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.doc.ast._

object BuildDoc {

  def buildTmplDoc(resource: ContextResource, block: TmplBlockContext): DocBlock = {
    val content = block.block.tmplDoc().content.tmplDocContent()
    DocBlock(addContext(resource, block), buildDocContent(resource, content))
  }

  def buildCodeBlock(resource: ContextResource, block: TmplDocContext): DocCodeBlock = {
    DocCodeBlock(None)
  }

  def buildAnyLevel(resource: ContextResource, anyLevel: TmplDocAnyLevelContext): DocAnyLevel = {
    DocAnyLevel(None)
  }

  def buildDocContent(resource: ContextResource, content: TmplDocContentContext): DocContent = {
    val contentType: DocContentType[_] = content match {
      case sec@_ if sec.tmplDocSec() != null => buildDocSec(resource, sec.tmplDocSec())
      case text@_ if text.tmplDocText() != null => buildDocText(resource, text.tmplDocText())
      case struct@_ if struct.tmplDocStruct() != null =>
    }
    DocContent(addContext(resource, content), contentType)
  }

  def buildDocImg(resource: ContextResource, img: TmplDocImgContext): DocImg = {
    DocImg(None)
  }

  def buildDocInclude(resource: ContextResource, include: TmplDocIncludeContext): DocInclude = {
    DocInclude(None)
  }

  def buildDocLink(resource: ContextResource, link: TmplDocLinkContext): DocLink = {
    DocLink(None)
  }

  def buildDocList(resource: ContextResource, list: TmplDocListContext): DocList = {
    DocList(None)
  }

  def buildDocSec(resource: ContextResource, section: TmplDocSecContext): DocSec = {
    DocSec(None)
  }

  def buildDocSpan(resource: ContextResource, span: TmplDocSpanContext): DocSpan = {
    DocSpan(None)
  }

  def buildDocStruct(resource: ContextResource, struct: TmplDocStructContext): DocStruct = {
    DocStruct(None)
  }

  def buildDocText(resource: ContextResource, text: TmplDocTextContext): DocText = {
    DocText(None)
  }

  def buildDocTable(resource: ContextResource, table: TmplDocTableContext): DocTable = {
    DocTable(None)
  }

}
