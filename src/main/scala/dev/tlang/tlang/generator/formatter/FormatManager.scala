package dev.tlang.tlang.generator.formatter

import dev.tlang.tlang.generator.langs.java.JavaFormatter

object FormatManager {

  val RET: String = System.lineSeparator()

  def findRules(name: String, rules: List[FormatRule]): List[FormatRule] = rules.filter(_.name.equals(name))

  def applyBlockRule(str: StringBuilder, rules: List[FormatRule], elem: String, ind: Indent): Indent = {
    var _ind = ind
    val selectRules = rules.filter(rule => rule.elem.equals(elem))
    _ind = applyFilteredRules(str, selectRules, "", _ind)
    _ind
  }

  def applyRules(str: StringBuilder, rules: List[FormatRule], elem: String, ind: Indent): Indent = {
    var _ind = ind
    val selectRules = rules.filter(rule => rule.elem.equals(elem))
    if (selectRules.isEmpty) str ++= elem
    else _ind = applyFilteredRules(str, selectRules, elem, _ind)
    _ind
  }

  def applyFilteredRules(str: StringBuilder, rules: List[FormatRule], elem: String, ind: Indent): Indent = {
    var _ind = ind
    rules.map(_.rule).foreach {
      case rule: LineReturnAfter =>
        indent(str, ind)
        str ++= elem
        for (_ <- 0 until rule.total) str ++= RET
        _ind = Indent(ind.ind, newLine = true)
      case rule: AddSpaceBefore =>
        val newInd = indent(str, ind)
        str ++= " "
        if (rule.addSeq) str ++= elem
        _ind = newInd
      case _: AddSpaceAfter =>
        val newInd = indent(str, ind)
        str ++= elem ++= " "
        _ind = newInd
      case rule: AddSpaceBeforeAndAfter =>
        val newInd = indent(str, ind)
        str ++= " " ++= elem ++= " "
        _ind = newInd
      case _: LineReturnAfterAndIndent =>
        // val ind1 = indent(str, ind)
        str ++= elem ++= RET
        val newInd = Indent(ind.ind + 1, newLine = true)
        _ind = newInd
      case _: LineReturnAndOutdentAndLineReturn =>
        //val ind1 = indent(str, ind)
        str ++= RET
        val newInd = Indent(ind.ind - 1, newLine = true)
        indent(str, newInd)
        str ++= elem ++= RET
        _ind = newInd
      case _: LineReturnAndOutdent =>
        str ++= RET
        val newInd = Indent(ind.ind - 1, newLine = true)
        indent(str, newInd)
        str ++= elem
        _ind = newInd
      case _: Outdent =>
        val newInd = Indent(ind.ind - 1, newLine = true)
        indent(str, newInd)
        str ++= elem
        _ind = newInd
      case _ =>
        val newInd = indent(str, ind)
        str ++= elem
        _ind = newInd
    }
    _ind
  }

  def indent(str: StringBuilder, indent: Indent): Indent = {
    if (indent.newLine) {
      for (_ <- 0 until indent.ind) str ++= JavaFormatter.spaces
      Indent(indent.ind)
    } else indent
  }

}
