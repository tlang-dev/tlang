package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl.{TmplNode, TmplReturn, TmplUse, TmplVar}
import dev.tlang.tlang.generator.formatter.{FormatManager, FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.dart.DartFormatter.END_OF_STATEMENT
import dev.tlang.tlang.generator.langs.dart.DartGenerator.includeKeyword

object GenericGenerator {

  def genPackage: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    _ind
  }

  def genUse: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplUse]
    val useRules = FormatManager.findRules("use", rules)
    _ind = Formatter.indent(str, indent)
    str ++= includeKeyword() ++= " '" ++= tmpl.parts.mkString("/").replaceFirst("/", ":").replace("/dart", ".dart") ++= "'"
    if (tmpl.alias.isDefined) str ++= " as " ++= tmpl.alias.get.toString
    _ind = FormatManager.applyRules(str, useRules, END_OF_STATEMENT, _ind)
    _ind
  }

  def genType: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    _ind
  }

  def genReturn: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplReturn]
    _ind = Formatter.indent(str, indent)
    str ++= "return" ++= " "
    _ind = followUp(tmpl.operation, str, _ind, rules)
    _ind
  }

  def genVar: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplVar]
    val varRules = FormatManager.findRules("var", rules)
    _ind = Formatter.indent(str, indent)
    if (tmpl.props.isEmpty && tmpl.`type`.isEmpty) {
      str ++= "var" ++= " "
    }
    if (tmpl.`type`.isDefined) {
      _ind = followUp(tmpl.`type`.get, str, _ind, rules)
      str ++= " "
    }
    str ++= tmpl.name.toString
    if (tmpl.isOptional) str ++= "?"
    if (tmpl.value.isDefined) {
      _ind = FormatManager.applyRules(str, varRules, "=", _ind)
      _ind = followUp(tmpl.value.get, str, _ind, rules)
    }
    _ind = FormatManager.applyRules(str, varRules, END_OF_STATEMENT, _ind)
    _ind
  }


}

