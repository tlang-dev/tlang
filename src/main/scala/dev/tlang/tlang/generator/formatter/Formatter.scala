package dev.tlang.tlang.generator.formatter

import dev.tlang.tlang.generator.Seq
import dev.tlang.tlang.generator.langs.java.JavaFormatter

object Formatter {

  val RET: String = System.lineSeparator()

  def format(seq: Seq, blocks: List[BlockSelector]): String = {
    val str = new StringBuilder()
    formatBlock(seq, blocks, List(), None, str, Indent())
    str.toString()
  }

  private def formatBlock(seq: Seq, blocks: List[BlockSelector], selectors: List[Selector], currentBlock: Option[BlockSelector], str: StringBuilder, ind: Indent): Indent = {
    var _ind = ind
    if (seq.blockName.isEmpty) {
      _ind = formatContent(seq, blocks, selectors, currentBlock, str, ind)
    } else {
      val matching = blocks.find(select => select.name == seq.blockName)
      if (matching.isDefined) matching.get.before.foreach(rule => _ind = applyBlockRule(rule, str, _ind))
      val openingRules = if (matching.isDefined) matching.get.opening else List()
      val contentRules = if (matching.isDefined) matching.get.content else List()
      val closingRules = if (matching.isDefined) matching.get.closing else List()
      if (seq.opening.isDefined) _ind = formatBlock(seq.opening.get, blocks, openingRules, matching, str, _ind)
      if (seq.child.isDefined) _ind = formatBlock(seq.child.get, blocks, contentRules, matching, str, _ind)
      _ind = formatChildren(seq, blocks, selectors, currentBlock, str, _ind)
      if (seq.closing.isDefined) _ind = formatBlock(seq.closing.get, blocks, closingRules, matching, str, _ind)
      if (matching.isDefined) matching.get.after.foreach(rule => _ind = applyBlockRule(rule, str, _ind))
    }
    _ind
  }

  private def formatContent(seq: Seq, blocks: List[BlockSelector], selectors: List[Selector], currentBlock: Option[BlockSelector], str: StringBuilder, ind: Indent): Indent = {
    var _ind = ind
    val matching = selectors.filter(select => select.token == seq.seq)
    val notMatching = selectors.filterNot(select => select.token == seq.seq)
    val applSelects = findApplicableRules(matching)
    if (applSelects.nonEmpty) {
      _ind = applyRules(seq, applSelects, str, 0, _ind)
    } else if (seq.seq.nonEmpty) {
      _ind = indent(str, _ind)
      str ++= seq.seq
    }
    val newSelectors: List[Selector] = matching.filter(selector => selector.children.nonEmpty).flatMap(_.children) ++ matching.filter(selector => selector.children.isEmpty) ++ notMatching
    //    seq.children.foreach(child => format(child, newSelectors, str, ind))
    //    if (seq.opening.isDefined) {
    //      _ind = applyOpening(seq.opening.get, newSelectors, str, _ind)
    //    }
    if (seq.child.isDefined) {
      _ind = formatBlock(seq.child.get, blocks, newSelectors, currentBlock, str, _ind)
    }

//        _ind = formatChildren(seq, blocks, newSelectors, currentBlock, str, _ind)

    _ind
  }

  def formatChildren(seq: Seq, blocks: List[BlockSelector], selectors: List[Selector], currentBlock: Option[BlockSelector], str: StringBuilder, ind: Indent): Indent = {
    var _ind = ind
    seq.children.toList.foreach(child => _ind = formatBlock(child, blocks, selectors, currentBlock, str, _ind))
    _ind
  }

  //  def applyOpening(seq: Seq, selectors: List[Selector], str: StringBuilder, ind: Indent): Indent = {
  //    str ++= seq.seq
  //    var _ind = ind
  //    for (child <- seq.child) {
  //      _ind = applyOpening(child, selectors, str, _ind)
  //    }
  //    _ind
  //  }

  def applyRules(seq: Seq, selectors: List[Selector], str: StringBuilder, i: Int, ind: Indent): Indent = {
    var res = applyRule(seq, selectors(i).rule.get, str, ind)
    if (i < selectors.length - 1) res = applyRules(seq, selectors, str, (i + 1), res)
    res
  }

  def findApplicableRules(selectors: List[Selector]): List[Selector] = {
    selectors.filter(select => select.children.isEmpty && select.rule.isDefined).sortWith((sel1, sel2) => sel1.level < sel2.level)
  }

  def applyBlockRule(rule: Rule, str: StringBuilder, ind: Indent): Indent = {
    rule match {
      case rule: LineReturn =>
        indent(str, ind)
        for (_ <- 0 until rule.total) str ++= RET
        Indent(ind.ind, newLine = true)
      case _ => ind
    }
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
