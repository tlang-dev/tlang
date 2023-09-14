package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.generator.formatter._

object DartFormatter {

  def RET: String = System.lineSeparator()

  def END_OF_STATEMENT = ";"

  def spaces: String = "  "

  def rules: List[FormatRule] = List(
    FormatRule("uses", "[END]", LineReturnAfter()),
    FormatRule("use", ";", LineReturnAfter()),
    FormatRule("var", ";", LineReturnAfter()),
    FormatRule("var", "=", AddSpaceBefore(addSeq = false)),
    FormatRule("var", "=", AddSpaceAfter()),
    FormatRule("impl", "{", AddSpaceBefore(addSeq = false)),
    FormatRule("impl", "{", LineReturnAfterAndIndent()),
    FormatRule("impl", "}", LineReturnAndOutdent()),
    FormatRule("exprBlock", "{", AddSpaceBefore(addSeq = false)),
    FormatRule("exprBlock", "{", LineReturnAfterAndIndent()),
    FormatRule("exprBlock", "}", LineReturnAndOutdent()),
  )

  def formatter(): List[BlockSelector] = {
    List(
      classBlock(),
      block(),
      ifBlock(),
      varBlock(),
      annotBlock(),
    )
  }

  private def classBlock(): BlockSelector = {
    BlockSelector("class",
      before = List(LineReturn()),
      opening = List(
        Selector("{", rule = Some(LineReturnAfterAndIndent()))
      ),
      closing = List(
        Selector("}", rule = Some(LineReturnAndOutdent()))
      ),
      after = List(LineReturn()),
    )
  }

  private def block(): BlockSelector = {
    BlockSelector("exprBlock",
      opening = List(
        Selector("{", rule = Some(LineReturnAfterAndIndent()))
      ),
      content = List(
        Selector(";", rule = Some(LineReturnAfter()))
      ),
      closing = List(
        Selector("}", rule = Some(LineReturnAndOutdentAndLineReturn()))
      )
    )
  }

  private def varBlock(): BlockSelector = {
    BlockSelector("var",
      //after = List(LineReturn()),
    )
  }

  private def ifBlock(): BlockSelector = {
    BlockSelector("if")
  }

  private def annotBlock(): BlockSelector = {
    BlockSelector("annot",
      after = List(LineReturn()),
    )
  }

  //  List(
  //      Selector("package", ListBuffer(Selector(";", rule = Some(LineReturnAfter(2))))),
  //      Selector(";", rule = Some(LineReturnAfter())),
  //   Selector("block",   ListBuffer(Selector("{", rule = Some(LineReturnAfterAndIndent())))),


  //      Selector("class", ListBuffer(Selector("{", rule = Some(AddSpaceBefore(false))))),
  //      Selector("class", ListBuffer(Selector("{", rule = Some(LineReturnAfterAndIndent())))),
  //      Selector("class", ListBuffer(Selector("}", rule = Some(LineReturnAndOutdentAndLineReturn())))),
  //      Selector("if", ListBuffer(Selector("{", rule = Some(AddSpaceBefore(false))))),
  //      Selector("if", rule = Some(LineReturnAfterAndIndent())),
  //      Selector("if", ListBuffer(Selector("}", rule = Some(LineReturnAndOutdentAndLineReturn())))),
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
  // )
  // }

}
