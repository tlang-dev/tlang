package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl.TmplNode
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.generator.formatter.{BlockSelector, FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator

object GenericFunc extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplFunc]
    _ind = Formatter.indent(str, indent)
//    if(tmpl.ret.isDefined) _ind = followUp(tmpl.ret.get, str, _ind, rules)
    str ++= tmpl.name.toString
    if (tmpl.content.isDefined) _ind = followUp(tmpl.content.get, str, _ind, rules)
    _ind
  }

}
