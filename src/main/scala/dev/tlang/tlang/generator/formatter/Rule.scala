package dev.tlang.tlang.generator.formatter

class Rule()

case class AddSpaceAfter(addSeq: Boolean = true) extends Rule

case class AddSpaceBefore(addSeq: Boolean = true) extends Rule

case class AddSpaceBeforeAndAfter() extends Rule

case class LineReturnAfter(total: Int = 1) extends Rule

case class LineReturnAfterAndIndent() extends Rule

case class LineReturnAndOutdentAndLineReturn() extends Rule

case class LineReturnAndOutdent() extends Rule

case class Outdent() extends Rule
