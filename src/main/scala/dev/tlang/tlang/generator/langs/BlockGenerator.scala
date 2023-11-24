package dev.tlang.tlang.generator.langs

import dev.tlang.tlang.generator.formatter.{FormatRule, Indent}
import dev.tlang.tlang.generator.langs.common.GenParameter
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

trait BlockGenerator {

  def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule], GenParameter) => Indent): Indent

}
