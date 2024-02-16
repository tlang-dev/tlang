package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.generator.formatter.{FormatManager, FormatRule, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter
import tlang.internal.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.LangBlock

object GenericBlock extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule], GenParameter) => Indent): Indent = {
    var _ind = indent
//    val tmpl = node.asInstanceOf[LangBlock]
//    if (tmpl.pkg.isDefined) followUp(tmpl.pkg.get, str, _ind, rules, params)
//    if (tmpl.uses.isDefined) tmpl.uses.get.foreach(use => {
//      _ind = followUp(use, str, _ind, rules, params)
//    })
//    val usesRules = FormatManager.findRules("uses", rules)
//    _ind = FormatManager.applyBlockRule(str, usesRules, "[END]", _ind)
//    if (tmpl.content.isDefined) tmpl.content.get.foreach(content => _ind = followUp(content, str, _ind, rules, params))
    _ind
  }
}
