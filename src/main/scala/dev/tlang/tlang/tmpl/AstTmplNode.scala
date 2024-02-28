package dev.tlang.tlang.tmpl

trait AstTmplNode extends AstValue {

  def getName: String

  def toEntity: AstEntity

  def toModel: AstModel

}
