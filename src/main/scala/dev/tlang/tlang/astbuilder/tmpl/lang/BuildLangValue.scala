package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.tmpl.TmplType
import dev.tlang.tlang.astbuilder.context.{ContextContent, ContextResource}
import dev.tlang.tlang.astbuilder.tmpl.lang.BuildLang.createAttrStr
import dev.tlang.tlang.astbuilder.{AstBuilderUtils, BuildAst, BuildHelperStatement}

object BuildLangValue {

  def buildVar(resource: ContextResource, func: TmplVarContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langVar.name)),
      Some(List(

      ))
    )
  }


  def buildValueType(resource: ContextResource, func: TmplValueTypeContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langValueType.name)),
      Some(List(

      ))
    )
  }


  def buildAffect(resource: ContextResource, func: TmplAffectContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langAffect.name)),
      Some(List(

      ))
    )
  }

  def buildPrimitive(resource: ContextResource, value: TmplPrimitiveValueContext): EntityValue = value match {
    case string@_ if string.tmplStringValue() != null => buildStringValue(resource, string.tmplStringValue())
    case number@_ if number.tmplNumberValue() != null => buildNumber(resource, number.tmplNumberValue())
    case text@_ if text.tmplTextValue() != null => buildText(resource, text.tmplTextValue())
    case entity@_ if entity.tmplEntityValue() != null => buildEntity(resource, entity.tmplEntityValue())
    case bool@_ if bool.tmplBoolValue() != null => buildBool(resource, bool.tmplBoolValue())
    case array@_ if array.tmplArrayValue() != null => buildArray(resource, None, array.tmplArrayValue())
  }

  def buildEntity(resource: ContextResource, string: TmplEntityValueContext): EntityValue = {
    val context = BuildAst.addContext(resource, string)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langEntity.name)),
      Some(List(

      ))
    )
  }


  def buildString(resource: ContextResource, string: TmplStringValueContext): EntityValue = {
    val context = BuildAst.addContext(resource, string)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langString.name)),
      Some(List(

      ))
    )
  }

  def buildArray(resource: ContextResource, `type`: Option[TmplType] = None, array: TmplArrayValueContext): EntityValue = {
    val context = BuildAst.addContext(resource, array)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langArray.name)),
      Some(List(

      ))
    )
  }

  //  def buildOptionId(resource: ContextResource, id: TmplIDContext): Option[EntityValue] = {
  //
  //  }

  def buildId(resource: ContextResource, id: TmplIDContext): EntityValue = id match {
    case id@_ if id.ID() != null => buildStringId(BuildAst.addContext(resource, id), id.ID().getSymbol.getText)
    case interId@_ if interId.tmplIntprID() != null => buildInterpretedId(BuildAst.addContext(resource, id), AstBuilderUtils.getText(interId.tmplIntprID().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprID().callObj()), AstBuilderUtils.getText(interId.tmplIntprID().pos))
    case escaped@_ if escaped.ESCAPED_ID() != null => buildStringId(BuildAst.addContext(resource, escaped), escaped.ESCAPED_ID().getText.replace("`", ""))
  }

  def buildStringId(context: Option[ContextContent], text: String): EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langStringId.name)),
      Some(List(
        createAttrStr(context, "value", text)
      ))
    )
  }

  def buildInterpretedId(context: Option[ContextContent], pre: Option[String] = None, call: CallObject, post: Option[String] = None): EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langInterpretedId.name)),
      Some(List(

      ))
    )
  }

  def buildString(resource: ContextResource, str: TmplStringContext): EntityValue = str match {
    case id@_ if id.STRING() != null => buildStringId(BuildAst.addContext(resource, str), AstBuilderUtils.extraString(id.STRING().getSymbol.getText))
    case interId@_ if interId.tmplIntprString() != null => buildInterpretedId(BuildAst.addContext(resource, str), AstBuilderUtils.getText(interId.tmplIntprString().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprString().callObj()), AstBuilderUtils.getText(interId.tmplIntprString().pos))
  }

  def buildText(resource: ContextResource, txt: TmplTextContext): EntityValue = txt match {
    case id@_ if id.TEXT() != null => buildStringId(BuildAst.addContext(resource, txt), AstBuilderUtils.extraText(id.TEXT().getSymbol.getText))
    case interId@_ if interId.tmplIntprText() != null => buildInterpretedId(BuildAst.addContext(resource, txt), AstBuilderUtils.getText(interId.tmplIntprText().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprText().callObj()), AstBuilderUtils.getText(interId.tmplIntprText().pos))
  }

  def buildStringValue(resource: ContextResource, string: TmplStringValueContext): EntityValue = {
    val context = BuildAst.addContext(resource, string)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langString.name)),
      Some(List(

      ))
    )
  }

  def buildNumber(resource: ContextResource, number: TmplNumberValueContext): EntityValue = {
    val value = number.value.getText
    if (value.contains(".")) buildDouble(BuildAst.addContext(resource, number), value.toDouble)
    else buildLong(BuildAst.addContext(resource, number), value.toLong)
  }

  def buildText(resource: ContextResource, text: TmplTextValueContext): EntityValue = {
    val context = BuildAst.addContext(resource, text)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langText.name)),
      Some(List(

      ))
    )
  }

  def buildBool(resource: ContextResource, bool: TmplBoolValueContext): EntityValue = {
    val context = BuildAst.addContext(resource, bool)
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langBool.name)),
      Some(List(

      ))
    )
  }

  def buildLong(context: Option[ContextContent], value: Long): EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langLong.name)),
      Some(List(

      ))
    )
  }

  def buildDouble(context: Option[ContextContent], value: Double): EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, TmplValueAst.langDouble.name)),
      Some(List(

      ))
    )
  }
}
