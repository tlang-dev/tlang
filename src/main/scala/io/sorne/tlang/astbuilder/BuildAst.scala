package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser
import io.sorne.tlang.ast.DomainModel

object BuildAst {

  def build(parser: TLangParser): DomainModel = {
    DomainModel()
  }

}
