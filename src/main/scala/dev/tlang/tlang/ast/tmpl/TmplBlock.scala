package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.context.Scope

case class TmplBlock(context: Option[ContextContent], name: String, lang: String,
                     var params: Option[List[HelperParam]],
                     var pkg: Option[TmplPkg] = None,
                     var uses: Option[List[TmplUse]] = None,
                     var content: Option[List[TmplContent]] = None,
                     scope: Scope = Scope()) extends DomainBlock with DeepCopy with AstContext {

  override def deepCopy(): TmplBlock =
    TmplBlock(context, name, lang, params,
      if (pkg.isDefined) Some(pkg.get.deepCopy()) else None,
      if (uses.isDefined) Some(uses.get.map(_.deepCopy())) else None,
      if (content.isDefined) Some(content.get.map(_.deepCopy().asInstanceOf[TmplContent])) else None,
      scope)

  override def getContext: Option[ContextContent] = context
}
