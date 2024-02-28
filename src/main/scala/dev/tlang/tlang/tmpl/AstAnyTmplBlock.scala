package dev.tlang.tlang.tmpl

trait AstAnyTmplBlock extends AstTmplNode {

  def getLangs: List[String]

  def getName: String

}
