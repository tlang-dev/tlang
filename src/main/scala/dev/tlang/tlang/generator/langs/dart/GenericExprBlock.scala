package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl.{TmplExprBlock, TmplNode}
import dev.tlang.tlang.generator.formatter.{FormatManager, FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter

object GenericExprBlock extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule], GenParameter) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplExprBlock]
    val expBlockRules = FormatManager.findRules("exprBlock", rules)
    _ind = FormatManager.applyRules(str, expBlockRules, "{", _ind)
    tmpl.exprs.foreach(content => {
      _ind = Formatter.indent(str, _ind)
      _ind = followUp(content, str, _ind, rules, params)
    })
    _ind = FormatManager.applyRules(str, expBlockRules, "}", _ind)
    _ind
  }
}
