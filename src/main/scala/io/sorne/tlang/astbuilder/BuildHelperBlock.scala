package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangHelperParser.HelperFuncContext
import io.sorne.tlang.TLangParser.HelperBlockContext
import io.sorne.tlang.ast.HelperBlock
import io.sorne.tlang.ast.helper.{HelperFunc, HelperStatement}

object BuildHelperBlock {

  def build(helperBlock: HelperBlockContext): HelperBlock = {
    HelperBlock()
  }

  def buildFunc(func: HelperFuncContext): HelperFunc = {
    HelperFunc(null, null, null)
  }

  def buildStatement(): HelperStatement = {
    null
  }
}
