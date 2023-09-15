package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl.TmplNode
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.generator.formatter.{FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator

object GenericFunc extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplFunc]
    _ind = Formatter.indent(str, indent)
    if (tmpl.ret.isDefined) tmpl.ret.get.foreach(ret => {
      _ind = followUp(ret, str, _ind, rules)
      str ++= " "
    })
    str ++= tmpl.name.toString
    if (tmpl.curries.isDefined) tmpl.curries.get.foreach(curry => _ind = followUp(curry, str, _ind, rules))
    if (tmpl.content.isDefined) _ind = followUp(tmpl.content.get, str, _ind, rules)
    _ind
  }

}
