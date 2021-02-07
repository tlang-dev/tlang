package io.sorne.tlang.ast.tmpl

import io.sorne.tlang.ast.DomainBlock
import io.sorne.tlang.ast.helper.HelperParam

case class TmplBlock(name: String, lang: String,
                     var params: Option[List[HelperParam]],
                     var pkg: Option[TmplPkg] = None,
                     var uses: Option[List[TmplUse]] = None,
                     var content: Option[List[TmplContent]] = None) extends DomainBlock