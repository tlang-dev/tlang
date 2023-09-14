package dev.tlang.tlang.generator.langs

import dev.tlang.tlang.ast.tmpl.TmplNode
import dev.tlang.tlang.generator.formatter.{FormatRule, Indent}

trait BlockGenerator {

  def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent): Indent

}
