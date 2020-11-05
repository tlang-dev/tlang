package io.sorne.tlang.ast.tmpl

import io.sorne.tlang.ast.DomainBlock
import io.sorne.tlang.ast.tmpl.func.TmplFunc

case class TmplBlock(pkg: Option[String] = None, uses: Option[List[TmplUse]] = Some(List()), impls: Option[List[TmplImpl]] = Some(List()), funcs: Option[List[TmplFunc]] = Some(List())) extends DomainBlock {

}
