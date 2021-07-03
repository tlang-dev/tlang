package dev.tlang.tlang.generator.formatter

import dev.tlang.tlang.generator.Seq
import dev.tlang.tlang.generator.langs.java.JavaFormatter

object Formatter {

  val RET: String = System.lineSeparator()

  def format(seq: Seq, selectors: List[Selector]): String = {
    val str = new StringBuilder()
    format(seq, selectors, str, Indent())
    str.toString()
  }

  def format(seq: Seq, selectors: List[Selector], str: StringBuilder, ind: Indent): Indent = {
    var _ind = ind
    val matching = selectors.filter(select => select.token == seq.seq)
    val notMatching = selectors.filterNot(select => select.token == seq.seq)
    val applSelects = findApplicableRules(matching)
    if (applSelects.nonEmpty) {
      _ind = applyRules(seq, applSelects, str, 0, _ind)
    } else if (seq.seq.nonEmpty) {
      _ind = indent(str, ind)
      str ++= seq.seq
    }
    val newSelectors: List[Selector] = matching.filter(selector => selector.children.nonEmpty).flatMap(_.children) ++ matching.filter(selector => selector.children.isEmpty) ++ notMatching
    //    seq.children.foreach(child => format(child, newSelectors, str, ind))
    for (child <- seq.children) {
      _ind = format(child, newSelectors, str, _ind)
    }
    _ind
  }

  def applyRules(seq: Seq, selectors: List[Selector], str: StringBuilder, i: Int, ind: Indent): Indent = {
    var res = applyRule(seq, selectors(i).rule.get, str, ind)
    if (i < selectors.length - 1) res = applyRules(seq, selectors, str, (i + 1), res)
    res
  }

  def findApplicableRules(selectors: List[Selector]): List[Selector] = {
    selectors.filter(select => select.children.isEmpty && select.rule.isDefined).sortWith((sel1, sel2) => sel1.level < sel2.level)
  }

  def applyRule(seq: Seq, rule: Rule, str: StringBuilder, ind: Indent): Indent = {
    rule match {
      case rule: LineReturnAfter =>
        indent(str, ind)
        str ++= seq.seq
        for (_ <- 0 until rule.total) str ++= RET
        Indent(ind.ind, newLine = true)
      case rule: AddSpaceBefore =>
        val newInd = indent(str, ind)
        str ++= " "
        if (rule.addSeq) str ++= seq.seq
        newInd
      case _: AddSpaceAfter =>
        val newInd = indent(str, ind)
        str ++= seq.seq ++= " "
        newInd
      case rule: AddSpaceBeforeAndAfter =>
        val newInd = indent(str, ind)
        str ++= " " ++= seq.seq ++= " "
        newInd
      case _: LineReturnAfterAndIndent =>
        // val ind1 = indent(str, ind)
        str ++= seq.seq ++= RET
        val newInd = Indent(ind.ind + 1, newLine = true)
        newInd
      case _: LineReturnAndOutdentAndLineReturn =>
        //val ind1 = indent(str, ind)
        str ++= RET
        val newInd = Indent(ind.ind - 1, newLine = true)
        indent(str, newInd)
        str ++= seq.seq ++= RET
        newInd
      case _: LineReturnAndOutdent =>
        str ++= RET
        str ++= seq.seq
        val newInd = Indent(ind.ind - 1, newLine = true)
        indent(str, newInd)
        newInd
      case _: Outdent =>
        val newInd = Indent(ind.ind - 1, newLine = true)
        indent(str, newInd)
        str ++= seq.seq
        newInd
      case _ =>
        val newInd = indent(str, ind)
        str ++= seq.seq
        newInd
    }
  }

  def indent(str: StringBuilder, indent: Indent): Indent = {
    if (indent.newLine) {
      for (_ <- 0 until indent.ind) str ++= JavaFormatter.spaces
      Indent(indent.ind)
    } else indent
  }

}
