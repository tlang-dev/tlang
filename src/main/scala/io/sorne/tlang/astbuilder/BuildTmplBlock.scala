package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser.{TmplExpressionContext, _}
import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import org.antlr.v4.runtime.Token

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

object BuildTmplBlock {

  def build(tmpl: TmplBlockContext): TmplBlock = {
    val pkg = if (tmpl.tmplPkg() != null && !tmpl.tmplPkg().isEmpty) Some(tmpl.tmplPkg().name.getText) else None
    val uses: List[TmplUse] = buildUses(tmpl.tmplUse())
    TmplBlock(tmpl.name.getText, tmpl.lang.getText,
      if (tmpl.params != null && !tmpl.params.isEmpty) Some(tmpl.params.asScala.toList.map(_.getText)) else None,
      pkg, Some(uses))
  }

  def buildUses(uses: java.util.List[TmplUseContext]): List[TmplUse] = {
    if (uses != null && !uses.isEmpty) uses.asScala.map(buildUse).toList
    else List()
  }

  def buildUse(use: TmplUseContext): TmplUse = {
    TmplUse(Utils.extraString(use.name.getText))
  }

  def buildImpl(impl: TmplImplContext): TmplImpl = {
    val contRet: List[TmplImplContent] = impl.tmplImplContents.asScala.map {
      case content@_ if content.tmplFunc() != null => build(content.tmplFunc())
      case content@_ if content.tmplExpression() != null => build(content.tmplExpression())
      case _ => new TmplImplContent()
    }.toList
    TmplImpl(impl.name.getText, buildFor(impl.forName, impl.forNames), Some(contRet))
  }

  def buildFor(for1: Token, fors: java.util.List[Token]): Option[List[TmplImplFor]] = {
    val forsRet = new ListBuffer[TmplImplFor]
    if (for1 != null && for1.getText != null && !for1.getText.isEmpty) forsRet += TmplImplFor(Utils.extraString(for1.getText))
    if (fors != null) fors.asScala.foreach(token => forsRet += TmplImplFor(Utils.extraString(token.getText)))
    if (forsRet.nonEmpty) Some(forsRet.toList)
    else None
  }

  def build(func: TmplFuncContext): TmplImplContent = {
    val curries =
      if (func.curries != null && !func.curries.isEmpty) Some(func.curries.asScala.map(build).toList)
      else None
    TmplFunc(func.name.getText, curries)
  }

  def build(curry: TmplCurryingContext): TmplFuncCurry = {
    TmplFuncCurry(Option(build(curry.param)))
  }

  def build(params: TmplCurryingParamContext): List[TmplParam] = {
    if (params.params != null && !params.params.isEmpty) params.params.asScala.map(build).toList
    else List()
  }

  def build(param: TmplParamContext): TmplParam = {
    TmplParam(param.name.getText, build(param.`type`))
  }

  def build(`type`: TmplTypeContext): TmplType = {
    TmplType(`type`.`type`.getText, build(`type`.generic), `type`.array != null)
  }

  def build(generic: TmplGenericContext): Option[TmplGeneric] = {
    if (generic != null && generic.types != null && !generic.types.isEmpty) Some(TmplGeneric(generic.types.asScala.map(build).toList))
    else None
  }

  def build(expr: TmplExpressionContext): TmplImplContent = {
    TmplImplExpression()
  }

}
