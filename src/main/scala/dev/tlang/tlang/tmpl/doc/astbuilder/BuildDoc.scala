package dev.tlang.tlang.tmpl.doc.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.astbuilder.AstBuilderUtils
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.tmpl.doc.ast._

import scala.jdk.CollectionConverters._

object BuildDoc {

  def buildTmplDoc(resource: ContextResource, block: TmplBlockContext): DocBlock = {
    val content = block.block.tmplDoc().content.tmplDocContent()
    DocBlock(addContext(resource, block), block.name.getText, block.lang.getText, None, buildDocContent(resource, content))
  }

  def buildCodeBlock(resource: ContextResource, block: TmplDocCodeBlockContext): DocCodeBlock = {
    DocCodeBlock(None, AstBuilderUtils.extraString(block.lang.getText), AstBuilderUtils.extraText(block.code.getText))
  }

  def buildAnyLevel(resource: ContextResource, anyLevel: TmplDocAnyLevelContext): DocAnyLevel = {
    DocAnyLevel(None)
  }

  private def buildDocContent(resource: ContextResource, content: TmplDocContentContext): DocContent = {
    val contentTypes: List[DocContentType[_]] = content.contents.asScala.toList.map {
      case sec@_ if sec.tmplDocSec() != null => buildDocSec(resource, sec.tmplDocSec())
      case text@_ if text.tmplDocText() != null => buildDocText(resource, text.tmplDocText())
      case struct@_ if struct.tmplDocStruct() != null => buildDocStruct(resource, struct.tmplDocStruct())
      case asis@_ if asis.tmplDocAsIs() != null => buildDocAsIs(resource, asis.tmplDocAsIs())
    }
    DocContent(addContext(resource, content), contentTypes)
  }

  def buildDocImg(resource: ContextResource, img: TmplDocImgContext): DocImg = {
    DocImg(None, AstBuilderUtils.extraString(img.src.getText), if (img.alt != null) Some(AstBuilderUtils.extraString(img.alt.getText)) else None)
  }

  def buildDocInclude(resource: ContextResource, include: TmplDocIncludeContext): DocInclude = {
    DocInclude(None)
  }

  def buildDocLink(resource: ContextResource, link: TmplDocLinkContext): DocLink = {
    DocLink(None, AstBuilderUtils.extraString(link.src.getText), AstBuilderUtils.extraString(link.name.getText))
  }

  def buildDocList(resource: ContextResource, list: TmplDocListContext): DocList = {
    DocList(None, AstBuilderUtils.extraString(list.order.getText), list.contents.asScala.toList.map {
      buildDocContent(resource, _)
    })
  }

  def buildDocSec(resource: ContextResource, section: TmplDocSecContext): DocSec = {
    DocSec(None, AstBuilderUtils.extraString(section.name.getText), buildDocContent(resource, section.content))
  }

  def buildDocSpan(resource: ContextResource, span: TmplDocSpanContext): DocSpan = {
    DocSpan(None)
  }

  def buildDocStruct(resource: ContextResource, struct: TmplDocStructContext): DocStruct = {
    val level = struct match {
      case _ if struct.LEVEL1() != null => 1
      case _ if struct.LEVEL2() != null => 2
      case _ if struct.LEVEL3() != null => 3
    }
    DocStruct(None, level, AstBuilderUtils.extraString(struct.title.getText).trim,
      if (struct.content != null) Some(buildDocContent(resource, struct.content)) else None
    )
  }

  def buildDocText(resource: ContextResource, text: TmplDocTextContext): DocText = {
    val content: DocTextType[_] = text match {
      case txt@_ if txt.tmplDocPlainText() != null => buildDocPlainText(resource, txt.tmplDocPlainText())
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

  def buildDocPlainText(resource: ContextResource, text: TmplDocPlainTextContext): DocPlainText = {
    DocPlainText(None, text.PLAIN_TEXT().getText.trim)
  }

  def buildDocAsIs(resource: ContextResource, asIs: TmplDocAsIsContext): DocAsIs = {
    DocAsIs(None, AstBuilderUtils.extraText(asIs.content.getText))
  }

}
