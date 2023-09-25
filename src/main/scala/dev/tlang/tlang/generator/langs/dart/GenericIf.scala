package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl.{TmplIf, TmplNode}
import dev.tlang.tlang.generator.formatter.{FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter

object GenericIf extends BlockGenerator {
  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule], GenParameter) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplIf]
    _ind = Formatter.indent(str, indent)
    str ++= "if"
    str ++= "("
    _ind = followUp(tmpl.cond, str, _ind, rules, params)
    str ++= ")"
    _ind = followUp(tmpl.content, str, _ind, rules, params)
    if (tmpl.elseBlock.isDefined) {
      str ++= " else "
      tmpl.elseBlock.get match {
        case Left(elseBlock) => _ind = followUp(elseBlock, str, _ind, rules, params)
        case Right(ifBlock) => _ind = followUp(ifBlock, str, _ind, rules, params)
      }
    }
    _ind
  }
}
