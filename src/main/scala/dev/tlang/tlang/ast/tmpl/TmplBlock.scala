package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Scope

case class TmplBlock(context: Option[ContextContent], name: String, lang: String,
                     var params: Option[List[HelperParam]],
                     var pkg: Option[TmplPkg] = None,
                     var uses: Option[List[TmplUse]] = None,
                     var specialised: Boolean = false,
                     var content: Option[List[EntityValue]] = None,
                     scope: Scope = Scope()) extends DomainBlock with DeepCopy with TmplNode[TmplBlock] {

  override def deepCopy(): TmplBlock =
    TmplBlock(context, name, lang, params,
      if (pkg.isDefined) Some(pkg.get.deepCopy()) else None,
      if (uses.isDefined) Some(uses.get.map(_.deepCopy())) else None,
      specialised,
      if (content.isDefined) Some(content.get.map(_.deepCopy())) else None,
      scope)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplBlock]): Int = 0

  override def getElement: TmplBlock = this

  override def getType: String = getClass.getName
}
