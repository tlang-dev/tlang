package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

trait TmplBlock[T] extends TmplNode[T] with DomainBlock {

}
