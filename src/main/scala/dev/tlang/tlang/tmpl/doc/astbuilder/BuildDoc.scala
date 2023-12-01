package dev.tlang.tlang.tmpl.doc.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.doc.ast._

import scala.jdk.CollectionConverters._

object BuildDoc {

  def buildTmplDoc(resource: ContextResource, block: TmplBlockContext): DocBlock = {
    val content = block.block.tmplDoc().content.tmplDocContent()
    DocBlock(addContext(resource, block), block.name.getText, block.lang.getText, None,buildDocContent(resource, content))
  }

  def buildCodeBlock(resource: ContextResource, block: TmplDocCodeBlockContext): DocCodeBlock = {
    DocCodeBlock(None)
  }

  def buildAnyLevel(resource: ContextResource, anyLevel: TmplDocAnyLevelContext): DocAnyLevel = {
    DocAnyLevel(None)
  }

  private def buildDocContent(resource: ContextResource, content: TmplDocContentContext): DocContent = {
    val contentTypes: List[DocContentType[_]] = content.contents.asScala.toList.map {
      //      case sec@_ if sec.tmplDocSec() != null => buildDocSec(resource, sec.tmplDocSec())
      case text@_ if text.tmplDocText() != null => buildDocText(resource, text.tmplDocText())
      //      case struct@_ if struct.tmplDocStruct() != null => buildDocStruct(resource, struct.tmplDocStruct())
    }
    DocContent(addContext(resource, content), contentTypes)
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
    DocStruct(None, null, null, null)
  }

  def buildDocText(resource: ContextResource, text: TmplDocTextContext): DocText = {
    val content: DocTextType[_] = text match {
      case txt@_ if txt.PLAIN_TEXT() != null => buildDocPlainText(resource, txt.PLAIN_TEXT().getText)
      case img@_ if img.tmplDocImg() != null => buildDocImg(resource, img.tmplDocImg())
      case link@_ if link.tmplDocLink() != null => buildDocLink(resource, link.tmplDocLink())
      case span@_ if span.tmplDocSpan() != null => buildDocSpan(resource, span.tmplDocSpan())
      case include@_ if include.tmplDocInclude() != null => buildDocInclude(resource, include.tmplDocInclude())
      case list@_ if list.tmplDocList() != null => buildDocList(resource, list.tmplDocList())
      case code@_ if code.tmplDocCodeBlock() != null => buildCodeBlock(resource, code.tmplDocCodeBlock())
      case table@_ if table.tmplDocTable() != null => buildDocTable(resource, table.tmplDocTable())
    }
    DocText(None, content)
  }

  def buildDocTable(resource: ContextResource, table: TmplDocTableContext): DocTable = {
    DocTable(None)
  }

  def buildDocPlainText(resource: ContextResource, text: String): DocPlainText = {
    DocPlainText(None, text)
  }

}
