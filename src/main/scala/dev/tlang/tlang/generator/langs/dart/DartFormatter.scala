package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.generator.formatter._

import scala.collection.mutable.ListBuffer

object DartFormatter {

  def RET: String = System.lineSeparator()

  def END_OF_STATEMENT = ";"

  def spaces: String = "  "

  def formatter(): List[Selector] = {
    List(
      //      Selector("package", ListBuffer(Selector(";", rule = Some(LineReturnAfter(2))))),
//      Selector(";", rule = Some(LineReturnAfter())),
      Selector("block",  rule = Some(LineReturnAfterAndIndent())),


      Selector("class", ListBuffer(Selector("{", rule = Some(AddSpaceBefore(false))))),
      Selector("class", ListBuffer(Selector("{", rule = Some(LineReturnAfterAndIndent())))),
      Selector("class", ListBuffer(Selector("}", rule = Some(LineReturnAndOutdentAndLineReturn())))),
      Selector("if", ListBuffer(Selector("{", rule = Some(AddSpaceBefore(false))))),
      Selector("if", ListBuffer(Selector("{", rule = Some(LineReturnAfterAndIndent())))),
      Selector("if", ListBuffer(Selector("}", rule = Some(LineReturnAndOutdentAndLineReturn())))),
      Selector(",", rule = Some(AddSpaceAfter())),
      Selector("==", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("!=", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("<", rule = Some(AddSpaceBeforeAndAfter())),
      Selector(">", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("<=", rule = Some(AddSpaceBeforeAndAfter())),
      Selector(">=", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("&&", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("||", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("=", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("+", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("-", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("*", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("/", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("%", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("<<", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("<<", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("&", rule = Some(AddSpaceBeforeAndAfter())),
      Selector("|", rule = Some(AddSpaceBeforeAndAfter())),
    )
  }

}
