package dev.tlang.tlang.generator.langs.java

import dev.tlang.tlang.generator.formatter._

import scala.collection.mutable.ListBuffer

object JavaFormatter {

  def RET: String = System.lineSeparator()

  def spaces: String = "    ";

  def formatter(): List[Selector] = {
    List(
      Selector("package", ListBuffer(Selector(";", rule = Some(LineReturnAfter(2))))),
      Selector("import", ListBuffer(Selector(";", rule = Some(LineReturnAfter())))),
      Selector("class", ListBuffer(Selector("{", rule = Some(AddSpaceBefore(false))))),
      Selector("class", ListBuffer(Selector("{", rule = Some(LineReturnAfterAndIndent())))),
      Selector("class", ListBuffer(Selector("}", rule = Some(LineReturnAndOutdentAndLineReturn())))),
    )
  }

}
