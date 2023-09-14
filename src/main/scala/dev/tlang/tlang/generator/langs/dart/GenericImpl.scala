package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl.{TmplImpl, TmplNode}
import dev.tlang.tlang.generator.formatter.{FormatManager, FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator

object GenericImpl extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplImpl]
    val implRules = FormatManager.findRules("impl", rules)
    _ind = Formatter.indent(str, indent)
    str ++= "class" ++= " " ++= tmpl.name.toString
    _ind = FormatManager.applyRules(str, implRules, "{", _ind)
    if (tmpl.content.isDefined) tmpl.content.get.foreach(content => {
      _ind = Formatter.indent(str, _ind)
      _ind = followUp(content, str, _ind, rules)
    })
    _ind = FormatManager.applyRules(str, implRules, "}", _ind)
    _ind
  }
}
