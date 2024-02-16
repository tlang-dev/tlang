package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.tmpl.lang.ast.func.LangFunc
import dev.tlang.tlang.generator.formatter.{FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter
import tlang.internal.TmplNode

object GenericFunc extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule], GenParameter) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangFunc]
    _ind = Formatter.indent(str, indent)
    if (tmpl.annots.isDefined) tmpl.annots.get.foreach(annot => _ind = followUp(annot, str, _ind, rules, params))
    if (tmpl.props.isDefined) {
      str ++= " "
//      _ind = followUp(tmpl.props.get, str, _ind, rules, params)
    }
    if (tmpl.ret.isDefined) tmpl.ret.get.foreach(ret => {
      _ind = followUp(ret, str, _ind, rules, params)
      str ++= " "
    })
    if (tmpl.preNames.isDefined) tmpl.preNames.get.foreach(name => {
      str ++= name.toString
      str ++= "."
    })
    str ++= tmpl.name.toString
    if (tmpl.curries.isDefined) tmpl.curries.get.foreach(curry => _ind = followUp(curry, str, _ind, rules, params))
    if (tmpl.postPros.isDefined) {
      str ++= " "
//      _ind = followUp(tmpl.postPros.get, str, _ind, rules, params)
    }
    if (tmpl.content.isDefined) _ind = followUp(tmpl.content.get, str, _ind, rules, params)
    _ind
  }

}
