package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.ast.helper.HelperParam
import dev.tlang.tlang.interpreter.context.Scope

case class TmplBlock(name: String, lang: String,
                     var params: Option[List[HelperParam]],
                     var pkg: Option[TmplPkg] = None,
                     var uses: Option[List[TmplUse]] = None,
                     var content: Option[List[TmplContent]] = None,
                     scope: Scope = Scope()) extends DomainBlock
