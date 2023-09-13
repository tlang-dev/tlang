package dev.tlang.tlang.generator.langs.xml

import dev.tlang.tlang.generator.formatter.{AddSpaceBefore, BlockSelector, LineReturnAfter, LineReturnAfterAndIndent, Outdent, Selector}

object XMLFormatter {

  def RET: String = System.lineSeparator()

  def spaces: String = "    "

  def formatter(): List[BlockSelector] = {
    List()
  }
//  def formatter(): List[Selector] = {
//    List(
//      Selector(">", rule = Some(LineReturnAfterAndIndent())),
//      Selector("</", rule = Some(Outdent())),
//      Selector("/>", rule = Some(AddSpaceBefore(addSeq = false))),
//      Selector("/>", rule = Some(LineReturnAfter())),
//    )
//  }
}
