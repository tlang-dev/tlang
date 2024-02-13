package dev.tlang.tlang.tmpl.common.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.{AstBuilderUtils, BuildHelperStatement}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import tlang.internal.{ContextResource, TmplID, TmplInterpretedId, TmplStringId}

object BuildCommonTmpl {

  def buildOptionId(resource: ContextResource, id: TmplIDContext): Option[TmplID] = {
    if (id != null && !id.isEmpty) Some(buildId(resource, id))
    else None
  }

  def buildId(resource: ContextResource, id: TmplIDContext): TmplID = id match {
    case id@_ if id.ID() != null => new TmplStringId(addContext(resource, id), id.ID().getSymbol.getText)
    case interId@_ if interId.tmplIntprID() != null => new TmplInterpretedId(addContext(resource, id), AstBuilderUtils.getText(interId.tmplIntprID().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprID().callObj()), AstBuilderUtils.getText(interId.tmplIntprID().pos))
    case escaped@_ if escaped.ESCAPED_ID() != null => new TmplStringId(addContext(resource, escaped), escaped.ESCAPED_ID().getText.replace("`", ""))
  }

  def buildString(resource: ContextResource, str: TmplStringContext): TmplID = str match {
    case id@_ if id.STRING() != null => new TmplStringId(addContext(resource, str), AstBuilderUtils.extraString(id.STRING().getSymbol.getText))
    case interId@_ if interId.tmplIntprString() != null => new TmplInterpretedId(addContext(resource, str), AstBuilderUtils.getText(interId.tmplIntprString().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprString().callObj()), AstBuilderUtils.getText(interId.tmplIntprString().pos))
  }

  def buildText(resource: ContextResource, txt: TmplTextContext): TmplID = txt match {
    case id@_ if id.TEXT() != null => new TmplStringId(addContext(resource, txt), AstBuilderUtils.extraText(id.TEXT().getSymbol.getText))
    case interId@_ if interId.tmplIntprText() != null => new TmplInterpretedId(addContext(resource, txt), AstBuilderUtils.getText(interId.tmplIntprText().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprText().callObj()), AstBuilderUtils.getText(interId.tmplIntprText().pos))
  }

  def buildString(resource: ContextResource, string: TmplStringValueContext): LangStringValue = LangStringValue(addContext(resource, string), buildString(resource, string.value))

  def buildNumber(resource: ContextResource, number: TmplNumberValueContext): LangPrimitiveValue[_] = {
    val value = number.value.getText
    if (value.contains(".")) LangDoubleValue(addContext(resource, number), value.toDouble)
    else LangLongValue(addContext(resource, number), value.toLong)
  }

  def buildText(resource: ContextResource, text: TmplTextValueContext): LangTextValue = LangTextValue(addContext(resource, text), buildText(resource, text.value))

  def buildBool(resource: ContextResource, bool: TmplBoolValueContext): LangBoolValue = LangBoolValue(addContext(resource, bool), bool.value.getText == "true")

  def buildIdOrString(resource: ContextResource, idOrString: TmplIdOrStringContext): Option[TmplID] = {
    if (idOrString != null && idOrString.tmplID() != null) Some(buildId(resource, idOrString.tmplID()))
    else if (idOrString != null && idOrString.tmplString() != null) Some(buildString(resource, idOrString.tmplString()))
    else None
  }

  def buildIdOrStringNoOption(resource: ContextResource, idOrString: TmplIdOrStringContext): TmplID = {
    if (idOrString.tmplID() != null) buildId(resource, idOrString.tmplID())
    else buildString(resource, idOrString.tmplString())
  }

}
