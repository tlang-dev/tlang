package dev.tlang.tlang.generator.langs.kotlin

import dev.tlang.tlang.generator.formatter._

import scala.collection.mutable.ListBuffer

object KotlinFormatter {

  def RET: String = System.lineSeparator()

  def spaces: String = "  "

  def formatter(): List[BlockSelector] = {
    List()
  }
//  def formatter(): List[Selector] = {
//    List(
//      Selector("package", ListBuffer(Selector(RET, rule = Some(LineReturnAfter())))),
////      Selector("import", ListBuffer(Selector(RET, rule = Some(LineReturnAfter())))),
//      Selector("class", ListBuffer(Selector("{", rule = Some(AddSpaceBefore(false))))),
//      Selector("class", ListBuffer(Selector("{", rule = Some(LineReturnAfterAndIndent())))),
//      Selector("class", ListBuffer(Selector("}", rule = Some(LineReturnAndOutdentAndLineReturn())))),
//      Selector(",", rule = Some(AddSpaceAfter())),
//      Selector("==", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("!=", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("<", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector(">", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("<=", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector(">=", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("&&", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("||", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("=", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("+", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("-", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("*", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("/", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("%", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("<<", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("<<", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("&", rule = Some(AddSpaceBeforeAndAfter())),
//      Selector("|", rule = Some(AddSpaceBeforeAndAfter())),
//    )
//  }

}
