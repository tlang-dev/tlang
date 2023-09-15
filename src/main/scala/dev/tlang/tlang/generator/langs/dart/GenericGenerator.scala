package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.TmplFuncCurry
import dev.tlang.tlang.ast.tmpl.primitive._
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
    val tmpl = node.asInstanceOf[TmplType]
    str ++= tmpl.name.toString
    if (tmpl.generic.isDefined) _ind = followUp(tmpl.generic.get, str, _ind, rules)
    if (tmpl.isArray) str ++= "[]"
    if (tmpl.instance.isDefined) _ind = followUp(tmpl.instance.get, str, _ind, rules)
    _ind
  }

  def genTypeCurry: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCurryParam]
    str ++= "("
    tmpl.params.foreach(params => params.zipWithIndex.foreach { case (param, i) =>
      _ind = followUp(param, str, _ind, rules)
      str ++= ","
      if (i != params.size - 1) str ++= ","
    })
    _ind
  }

  def genGeneric: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplGeneric]
    _ind = Formatter.indent(str, indent)
    str ++= "<"
    tmpl.types.zipWithIndex.foreach { case (t, i) =>
      _ind = followUp(t, str, _ind, rules)
      if (i != tmpl.types.size - 1) str ++= ","
    }
    str ++= ">"

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

  def genFuncCurry: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplFuncCurry]
    str ++= "("
    tmpl.params.foreach(params => params.foreach(param => _ind = followUp(param, str, _ind, rules)))
    str ++= ")"
    _ind
  }

  def genParam: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplParam]
    if (tmpl.annots.isDefined) tmpl.annots.get.foreach(annot => _ind = followUp(annot, str, _ind, rules))
    if (tmpl.`type`.isDefined) {
      _ind = followUp(tmpl.`type`.get, str, _ind, rules)
      str ++= " "
    }
    str ++= tmpl.name.toString
    _ind
  }

  def genOperation: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplOperation]
    tmpl.content match {
      case Left(block) => {
        str ++= "("
        _ind = followUp(block, str, _ind, rules)
        str ++= ")"
      }
      case Right(cond) => _ind = followUp(cond, str, _ind, rules)
    }
    if (tmpl.next.isDefined) {
      str ++= genOperator(tmpl.next.get._1)
      _ind = followUp(tmpl.next.get._2, str, _ind, rules)
    }
    _ind
  }

  def genCallLink: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCallObjectLink]
    str ++= tmpl.link
    _ind = followUp(tmpl.call, str, _ind, rules)
    _ind
  }

  def genCallObject: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCallObj]
    tmpl.props.foreach(prop => followUp(prop, str, _ind, rules))
    _ind = followUp(tmpl.firstCall, str, _ind, rules)
    tmpl.calls.foreach(link => _ind = followUp(link, str, _ind, rules))
    _ind
  }

  def genCallVar: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCallVar]
    str ++= tmpl.name.toString
    _ind
  }

  def genCallArray: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCallArray]
    str ++= tmpl.name.toString
    str ++= "["
    _ind = followUp(tmpl.elem, str, _ind, rules)
    str ++= "]"
    _ind
  }


  //  def genCallObjType: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
  //    var _ind = indent
  //    val tmpl = node.asInstanceOf[TmplCallObjType[_]]
  //    tmpl match {
  //      case array: TmplCallArray => _ind = followUp(array, str, _ind, rules)
  //      case func: TmplCallFunc => _ind = followUp(func, str, _ind, rules)
  //      case variable: TmplCallVar => _ind = followUp(variable, str, _ind, rules)
  //    }
  //    _ind
  //  }

  def genCallFunc: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCallFunc]
    str ++= tmpl.name.toString
    if (tmpl.currying.isDefined) {
      tmpl.currying.foreach(_.foreach(curry => {
        str ++= "("
        curry.params.foreach(param => param.foreach(attr => _ind = followUp(attr, str, _ind, rules)))
        str ++= ")"
      }))
    } else str ++= "()"
    _ind
  }

  def genAnonFunc: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplAnonFunc]
    _ind = followUp(tmpl.currying, str, _ind, rules)
    if (tmpl.content.isInstanceOf[TmplExpression[_]]) str ++= "=>"
    _ind = followUp(tmpl.content, str, _ind, rules)
    _ind
  }

  def genSpecialBlock: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplSpecialBlock]
    _ind = Formatter.indent(str, indent)
    str ++= genSpecialType(tmpl.`type`) ++= " "
    if (tmpl.curries.isDefined) tmpl.curries.get.foreach(curry => _ind = followUp(curry, str, _ind, rules))
    if (tmpl.content.isDefined) _ind = followUp(tmpl.content.get, str, _ind, rules)
    _ind
  }

  def genSpecialType(name: String): String = name match {
    case "future" => "async"
    case _ => name
  }

  def genSetAttribute: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplSetAttribute]
    if (tmpl.name.isDefined) {
      str ++= genOptTmplID(tmpl.name)
      str ++= ":"
    }
    _ind = followUp(tmpl.value, str, _ind, rules)
    _ind
  }

  def genEntityValue: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplEntityValue]
    if (tmpl.name.isDefined) str ++= tmpl.name.get.toString
    str ++= "("
    //    if(tmpl.attrs.isDefined)_ind = followUp(tmpl.attrs.g, str, _ind, rules)
    str ++= ")"
    _ind
  }

  def genAttribute: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplAttribute]
    if (tmpl.`type`.isDefined) str ++= tmpl.`type`.get.toString ++= ":"
    str ++= "("
    _ind = followUp(tmpl.value, str, _ind, rules)
    str ++= ")"
    _ind
  }

  def genStringValue: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    val tmpl = node.asInstanceOf[TmplStringValue]
    str ++= "\""
    str ++= tmpl.value.toString
    str ++= "\""
    indent
  }

  def genTextValue: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    val tmpl = node.asInstanceOf[TmplTextValue]
    str ++= "\""
    str ++= tmpl.value.toString
    str ++= "\""
    indent
  }

  def genLongValue: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    val tmpl = node.asInstanceOf[TmplLongValue]
    str ++= tmpl.value.toString
    indent
  }

  def genDoubleValue: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    val tmpl = node.asInstanceOf[TmplDoubleValue]
    str ++= tmpl.value.toString
    indent
  }

  def genBoolValue: BlockGenerator = (node: TmplNode[_], str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (TmplNode[_], StringBuilder, Indent, List[FormatRule]) => Indent) => {
    val tmpl = node.asInstanceOf[TmplBoolValue]
    if (tmpl.value) str ++= "true" else str ++= "false"
    indent
  }

  def genOptTmplID(tmplId: Option[TmplID]): String = {
    if (tmplId.isDefined) genTmplID(tmplId.get)
    else ""
  }

  def genTmplID(tmplId: TmplID): String = {
    tmplId match {
      case str: TmplStringID =>
        var seq = "\""
        seq += str.toString
        seq += "\""
        seq
      case _ => tmplId.toString
    }
  }

  def genOperator(op: Operator.operator): String = {
    op match {
      case Operator.OR => "||"
      case Operator.AND => "&&"
      case Operator.ADD => "+"
      case Operator.SUBTRACT => "-"
      case Operator.MULTIPLY => "*"
      case Operator.DIVIDE => "/"
      case Operator.MODULO => "%"
      case Operator.EQUAL => "=="
      case Operator.GREATER => ">"
      case Operator.LESSER => "<"
      case Operator.GREATER_OR_EQUAL => ">="
      case Operator.LESSER_OR_EQUAL => "<="
      case Operator.NOT_EQUAL => "!="
    }
  }
}
