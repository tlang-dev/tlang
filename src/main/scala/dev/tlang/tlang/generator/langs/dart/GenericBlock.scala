package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplNode}
import dev.tlang.tlang.generator.formatter.{FormatManager, FormatRule, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator

object GenericBlock extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplBlock]
    if (tmpl.pkg.isDefined) followUp(tmpl.pkg.get, str, _ind, rules)
    if (tmpl.uses.isDefined) tmpl.uses.get.foreach(use => {
      _ind = followUp(use, str, _ind, rules)
    })
    val usesRules = FormatManager.findRules("uses", rules)
    _ind = FormatManager.applyBlockRule(str, usesRules, "[END]", _ind)
    if (tmpl.content.isDefined) tmpl.content.get.foreach(content => _ind = followUp(content, str, _ind, rules))
    _ind
  }
}
