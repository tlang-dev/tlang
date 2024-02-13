package dev.tlang.tlang.tmpl.style.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource
import dev.tlang.tlang.astbuilder.{BuildHelperBlock, BuildHelperStatement}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.common.astbuilder.BuildCommonTmpl
import dev.tlang.tlang.tmpl.style.ast._
import tlang.internal.ContextResource

import scala.jdk.CollectionConverters._

object BuildStyle {

  def buildStyle(resource: ContextResource, style: TmplStyleContext): StyleBlock = {
    StyleBlock(addContext(resource, style), style.name.getText, style.langs.asScala.map(_.getText).toList,
      if (style.params != null && !style.params.isEmpty) Some(BuildHelperBlock.buildParams(resource, style.params.asScala.toList).map(param => NativeType(param.context, param))) else None,
      style.content.blocks.asScala.map(buildStyleStruct(resource, _)).toList)
  }

//  def buildStyleBlock(resource: ContextResource, block: StyleSetAttribute): List[StyleStruct] = {
//    StyleStruct(addContext(resource, block), block.name.getText, block.langs.asScala.map(_.getText).toList,
//      if (block.params != null && !block.params.isEmpty) Some(BuildHelperBlock.buildParams(resource, block.params.asScala.toList).map(param => NativeType(param.context, param))) else None,
//      buildStyleStruct(resource, block.content))
//  }

  def buildStyleStruct(resource: ContextResource, struct: StyleStructContext): StyleStruct = {
    StyleStruct(addContext(resource, struct), BuildCommonTmpl.buildOptionId(resource, struct.name), if (struct.params != null && !struct.params.isEmpty) Some(struct.params.asScala.map(BuildStyle.buildStyleAttribute(resource, _)).toList) else None,
      if (struct.attrs != null && !struct.attrs.isEmpty) Some(struct.attrs.asScala.map(BuildStyle.buildStyleAttribute(resource, _)).toList) else None)
  }

  def buildStyleAttribute(resource: ContextResource, attr: StyleAttributeContext): StyleAttribute[_] = {
    attr match {
      case attr@_ if attr.styleSetAttribute() != null => buildSetAttribute(resource, attr.styleSetAttribute())
      case incl@_ if incl.styleInclude() != null => buildInclude(resource, incl.styleInclude())
    }
  }

  def buildSetAttribute(resource: ContextResource, attr: StyleSetAttributeContext): StyleSetAttribute = {
    StyleSetAttribute(addContext(resource, attr), BuildCommonTmpl.buildIdOrString(resource, attr.name), buildStyleValue(resource, attr.value))
  }

  def buildInclude(resource: ContextResource, incl: StyleIncludeContext): StyleInclude = {
    val context = addContext(resource, incl)
    StyleInclude(context, NativeType(context, BuildHelperStatement.buildCallObject(resource, incl.callObj())))
  }

  def buildStyleValue(resource: ContextResource, attr: StyleValueContext): TmplNode[_] = {
    attr match {
      case array@_ if array.styleArrayValue() != null => buildArray(resource, attr.styleArrayValue())
      case number@_ if number.tmplNumberValue() != null => BuildCommonTmpl.buildNumber(resource, number.tmplNumberValue())
      case string@_ if string.tmplIdOrString() != null => BuildCommonTmpl.buildIdOrStringNoOption(resource, string.tmplIdOrString())
      case bool@_ if bool.tmplBoolValue() != null => BuildCommonTmpl.buildBool(resource, bool.tmplBoolValue())
    }
  }

  def buildArray(resource: ContextResource, array: StyleArrayValueContext): StyleArray = {
    StyleArray(addContext(resource, array), array.params.asScala.map(buildStyleAttribute(resource, _)).toList)
  }

}
