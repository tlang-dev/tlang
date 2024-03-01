package dev.tlang.tlang.generator.langs

import dev.tlang.tlang.generator.formatter.{FormatRule, Indent}
import dev.tlang.tlang.generator.langs.common.GenParameter
import dev.tlang.tlang.tmpl.AstTmplNode
import tlang.internal.TmplNode

trait BlockGenerator {

  def generate(node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent): Indent

}
