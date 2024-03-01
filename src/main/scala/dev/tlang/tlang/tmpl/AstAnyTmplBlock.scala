package dev.tlang.tlang.tmpl

import tlang.internal.DomainBlock

trait AstAnyTmplBlock extends AstTmplNode with DomainBlock {

  def getLangs: List[String]

  def getName: String

}
