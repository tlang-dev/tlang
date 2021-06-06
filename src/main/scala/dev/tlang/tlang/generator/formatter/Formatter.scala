package dev.tlang.tlang.generator.formatter

import dev.tlang.tlang.generator.Seq
import dev.tlang.tlang.generator.langs.java.JavaFormatter

object Formatter {

  val RET: String = System.lineSeparator()

  def format(seq: Seq, selectors: List[Selector]): String = {
    val str = new StringBuilder()
    format(seq, selectors, str, 0)
    str.toString()
  }

  def format(seq: Seq, selectors: List[Selector], str: StringBuilder, indent: Int): Int = {
    var ind = indent
    val matching = selectors.filter(select => select.token == seq.seq)
    val notMatching = selectors.filterNot(select => select.token == seq.seq)
    val applSelects = findApplicableRules(matching)
    if (applSelects.nonEmpty) {
      ind = applyRules(seq, applSelects, str, 0, ind)
    } else str ++= seq.seq
    val newSelectors: List[Selector] = matching.filter(selector => selector.children.nonEmpty).flatMap(_.children) ++ notMatching
    //    seq.children.foreach(child => format(child, newSelectors, str, ind))
    for (child <- seq.children) {
      ind = format(child, newSelectors, str, ind)
    }
    ind
  }

  def applyRules(seq: Seq, selectors: List[Selector], str: StringBuilder, i: Int, ind: Int): Int = {
    var res = applyRule(seq, selectors(i).rule.get, str, ind)
    if (i < selectors.length - 1) res = applyRules(seq, selectors, str, (i + 1), res)
    res
  }

  def findApplicableRules(selectors: List[Selector]): List[Selector] = {
    selectors.filter(select => select.children.isEmpty && select.rule.isDefined).sortWith((sel1, sel2) => sel1.level < sel2.level)
  }

  def applyRule(seq: Seq, rule: Rule, str: StringBuilder, ind: Int): Int = {
    rule match {
      case rule: LineReturnAfter =>
        str ++= seq.seq
        for (_ <- 0 until rule.total) str ++= RET
        indent(str, ind)
        ind
      case rule: AddSpaceBefore => str ++= " "
        if (rule.addSeq) str ++= seq.seq
        ind
      case _: AddSpaceAfter => str ++= seq.seq ++= " "
        ind
      case _: LineReturnAfterAndIndent =>
        str ++= seq.seq ++= RET
        val newInd = ind + 1
        indent(str, newInd)
        newInd
      case _: LineReturnAndOutdentAndLineReturn =>
        val newIdent = ind - 1
        str ++= RET
        indent(str, newIdent)
        str ++= seq.seq ++= RET
        indent(str, newIdent)
        newIdent
      case _ =>
        str ++= seq.seq
        ind
    }
  }

  def indent(str: StringBuilder, indent: Int): Unit = {
    for (_ <- 0 until indent) str ++= JavaFormatter.spaces
  }

  //  def format(seq: Seq, selectors: List[Selector], indent: Int = 0): String = {
  //
  //  }

}
