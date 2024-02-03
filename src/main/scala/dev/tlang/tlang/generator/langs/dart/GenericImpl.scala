package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.generator.formatter.{FormatManager, FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.LangImpl

import scala.collection.mutable.{Map => MMap}

object GenericImpl extends BlockGenerator {

  override def generate(node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule], GenParameter) => Indent): Indent = {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangImpl]
    val implRules = FormatManager.findRules("impl", rules)
    val noEOS = params.copy(addEOS = false)
    _ind = Formatter.indent(str, indent)
    if (tmpl.annots.isDefined) tmpl.annots.get.foreach(annot => _ind = followUp(annot, str, _ind, rules, noEOS))
    str ++= "class" ++= " " ++= tmpl.name.toString
    if (tmpl.fors.isDefined) {
      str ++= " extends "
      tmpl.fors.get.types.zipWithIndex.foreach { case (incl, i) =>
        _ind = followUp(incl, str, _ind, rules, noEOS)
        if (i != tmpl.fors.get.types.size - 1) str ++= ","
      }
    }
    if (tmpl.withs.isDefined) {
      str ++= " implements "
      tmpl.withs.get.types.zipWithIndex.foreach { case (incl, i) =>
        _ind = followUp(incl, str, _ind, rules, noEOS)
        if (i != tmpl.fors.get.types.size - 1) str ++= ","
      }
    }
    _ind = FormatManager.applyRules(str, implRules, "{", _ind)
    if (tmpl.content.isDefined) tmpl.content.get.foreach(content => {
      _ind = Formatter.indent(str, _ind)
      _ind = followUp(content, str, _ind, rules, params.copy(addEOS = true))
    })
    _ind = FormatManager.applyRules(str, implRules, "}", _ind)
    _ind
  }
}
