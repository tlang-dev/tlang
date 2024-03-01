package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.generator.formatter.{FormatManager, FormatRule, Formatter, Indent}
import dev.tlang.tlang.generator.langs.BlockGenerator
import dev.tlang.tlang.generator.langs.common.GenParameter
import dev.tlang.tlang.generator.langs.dart.DartFormatter.END_OF_STATEMENT
import dev.tlang.tlang.generator.langs.dart.DartGenerator.includeKeyword
import dev.tlang.tlang.tmpl.AstTmplNode
import tlang.internal.TmplNode
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.LangAnnotationParam
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import tlang.internal.{TmplID, TmplStringID}

object GenericGenerator {

  def genPackage: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    _ind
  }

  def genUse: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangUse]
    val useRules = FormatManager.findRules("use", rules)
    _ind = Formatter.indent(str, indent)
    str ++= includeKeyword() ++= " '" ++= tmpl.parts.mkString("/").replaceFirst("/", ":").replace("/dart", ".dart") ++= "'"
    if (tmpl.alias.isDefined) str ++= " as " ++= tmpl.alias.get.toString
    _ind = FormatManager.applyRules(str, useRules, END_OF_STATEMENT, _ind)
    _ind
  }

/*  def genType: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplType]
    str ++= tmpl.name.toString
    if (tmpl.generic.isDefined) _ind = followUp(tmpl.generic.get, str, _ind, rules, params)
    if (tmpl.isArray) str ++= "[]"
    if (tmpl.instance.isDefined) _ind = followUp(tmpl.instance.get, str, _ind, rules, params)
    _ind
  }*/

 /* def genTypeCurry: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCurryParam]
    str ++= "("
    tmpl.params.foreach(paramz => {
      if (paramz.`type`.equals("MAND")) str ++= "{"
      else if (paramz.`type`.equals("POS")) str ++= "["
      paramz.params.foreach(_.zipWithIndex.foreach { case (param, i) =>
        _ind = followUp(param, str, _ind, rules, params)
        if (i != paramz.params.size - 1) str ++= ","
      })
      if (paramz.`type`.equals("MAND")) str ++= "}"
      else if (paramz.`type`.equals("POS")) str ++= "]"
    })
    _ind
  }*/

  def genGeneric: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangGeneric]
    _ind = Formatter.indent(str, indent)
    str ++= "<"
    tmpl.types.zipWithIndex.foreach { case (t, i) =>
      _ind = followUp(t, str, _ind, rules, params)
      if (i != tmpl.types.size - 1) str ++= ","
    }
    str ++= ">"

    _ind
  }

  def genReturn: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangReturn]
    _ind = Formatter.indent(str, indent)
    str ++= "return" ++= " "
//    _ind = followUp(tmpl.operation, str, _ind, rules, params.copy(addEOS = false))
    if (params.addEOS) str ++= DartFormatter.END_OF_STATEMENT
    _ind
  }

  def genVar: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangVar]
    val varRules = FormatManager.findRules("var", rules)
    val noEOS = params.copy(addEOS = false)
    _ind = Formatter.indent(str, indent)
    if (tmpl.annots.isDefined) tmpl.annots.get.foreach(annot => _ind = followUp(annot, str, _ind, rules, noEOS))
    if (tmpl.props.isDefined) {
//      _ind = followUp(tmpl.props.get, str, _ind, rules, noEOS)
      str ++= " "
    }
    if (tmpl.props.isEmpty && tmpl.`type`.isEmpty) {
      str ++= "var" ++= " "
    }
    if (tmpl.`type`.isDefined) {
      _ind = followUp(tmpl.`type`.get, str, _ind, rules, noEOS)
      if (tmpl.isOptional) str ++= "?"
      str ++= " "
    }
    str ++= tmpl.name.toString
    if (tmpl.value.isDefined) {
      _ind = FormatManager.applyRules(str, varRules, "=", _ind)
//      _ind = followUp(tmpl.value.get, str, _ind, rules, noEOS)
    }
    if (params.addEOS) _ind = FormatManager.applyRules(str, varRules, END_OF_STATEMENT, _ind)
    _ind
  }

 /* def genFuncCurry: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplFuncCurry]
    str ++= "("
    tmpl.params.foreach(paramz => paramz.zipWithIndex.foreach {
      case (param, i) =>
        _ind = followUp(param, str, _ind, rules, params)
        if (i != tmpl.params.get.size - 1) str ++= ","
    })
    str ++= ")"
    _ind
  }*/

  def genParam: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangParam]
    if (tmpl.annots.isDefined) tmpl.annots.get.foreach(annot => _ind = followUp(annot, str, _ind, rules, params))
    if (tmpl.`type`.isDefined) {
      _ind = followUp(tmpl.`type`.get, str, _ind, rules, params)
      str ++= " "
    }
//    str ++= tmpl.name.toString
    _ind
  }

  def genProps: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangProp]
    tmpl.props.zipWithIndex.foreach { case (prop, i) =>
      //      _ind = followUp(prop, str, _ind, rules, params)
      str ++= prop.toString
      if (i != tmpl.props.size - 1) str ++= " "
    }
    _ind
  }

  def genOperation: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangOperation]
    tmpl.content match {
      case Left(block) => {
        str ++= "("
//        _ind = followUp(block, str, _ind, rules, params)
        str ++= ")"
      }
      case Right(cond) => _ind = followUp(cond, str, _ind, rules, params)
    }
    if (tmpl.next.isDefined) {
      str ++= genOperator(tmpl.next.get._1)
//      _ind = followUp(tmpl.next.get._2, str, _ind, rules, params)
    }
    _ind
  }

  def genCallLink: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangCallObjectLink]
    str ++= tmpl.link
    _ind = followUp(tmpl.call, str, _ind, rules, params)
    _ind
  }

  def genCallObject: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangCallObj]
    tmpl.props.foreach(prop => {
//      followUp(prop, str, _ind, rules, params)
      str ++= " "
    })
    _ind = followUp(tmpl.firstCall, str, _ind, rules, params)
    val noEOS = params.copy(addEOS = false)
//    tmpl.calls.foreach(link => _ind = followUp(link, str, _ind, rules, noEOS))
    if (params.addEOS) str ++= DartFormatter.END_OF_STATEMENT
    _ind
  }

  def genCallVar: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangCallVar]
//    str ++= tmpl.name.toString
    _ind
  }

  def genCallArray: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangCallArray]
    str ++= tmpl.name.toString
    str ++= "["
//    _ind = followUp(tmpl.elem, str, _ind, rules, params)
    str ++= "]"
    _ind
  }

  def genAffect: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangAffect]
    _ind = followUp(tmpl.variable, str, _ind, rules, params)
    str ++= "="
//    _ind = followUp(tmpl.value, str, _ind, rules, params)
    _ind
  }

  def genAnnot: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangAnnotation]
    str ++= "@"
    str ++= tmpl.name.toString
    if (tmpl.values.isDefined) {
      str ++= "("
      tmpl.values.get.zipWithIndex.foreach { case (value, i) =>
//        _ind = followUp(value, str, _ind, rules, params)
        if (i != tmpl.values.get.size - 1) str ++= ","
      }
      str ++= ")"
    }
    _ind
  }

  def genAnnotParam: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangAnnotationParam]
    if (tmpl.name.isDefined) str ++= tmpl.name.get.toString
    str ++= "="
    _ind = followUp(tmpl.value, str, _ind, rules, params)
    _ind
  }


  //  def genCallObjType: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule]) => Indent) => {
  //    var _ind = indent
  //    val tmpl = node.asInstanceOf[TmplCallObjType[_]]
  //    tmpl match {
  //      case array: TmplCallArray => _ind = followUp(array, str, _ind, rules, params)
  //      case func: TmplCallFunc => _ind = followUp(func, str, _ind, rules, params)
  //      case variable: TmplCallVar => _ind = followUp(variable, str, _ind, rules, params)
  //    }
  //    _ind
  //  }

  /*def genCallFunc: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplCallFunc]
    str ++= tmpl.name.toString
    val noEOS = params.copy(addEOS = false)
    if (tmpl.currying.isDefined) {
      tmpl.currying.foreach(_.foreach(curry => {
        str ++= "("
        curry.params.foreach(param => param.params.foreach(_.zipWithIndex.foreach {
          case (attr, i) =>
            _ind = followUp(attr, str, _ind, rules, noEOS)
            if (i != param.params.get.size - 1) str ++= ","
        }))
        str ++= ")"
      }))
    } else str ++= "()"
    _ind
  }*/

 /* def genAnonFunc: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[TmplAnonFunc]
    _ind = followUp(tmpl.currying, str, _ind, rules, params)
    if (tmpl.content.isInstanceOf[TmplExpression[_]] && !tmpl.content.isInstanceOf[TmplSpecialBlock]) {
      str ++= "=>"
      _ind = followUp(tmpl.content, str, _ind, rules, params.copy(addEOS = false))
    } else _ind = followUp(tmpl.content, str, _ind, rules, params.copy(addEOS = true))
    _ind
  }*/

  def genSpecialBlock: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangSpecialBlock]
    _ind = Formatter.indent(str, indent)
    //    val noEOS = params.copy(addEOS = false)
    str ++= genSpecialType(tmpl.`type`) ++= " "
    if (tmpl.curries.isDefined) tmpl.curries.get.foreach(curry => _ind = followUp(curry, str, _ind, rules, params))
    if (tmpl.content.isDefined && tmpl.content.get.isInstanceOf[LangExprBlock]) _ind = followUp(tmpl.content.get, str, _ind, rules, params.copy(addEOS = true))
    else if (tmpl.content.isDefined) _ind = followUp(tmpl.content.get, str, _ind, rules, params)
    _ind
  }

  def genSpecialType(name: String): String = name match {
    case "future" => "async"
    case _ => name
  }

  def genSetAttribute: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangSetAttribute]
    if (tmpl.name.isDefined) {
      str ++= tmpl.name.get.toString
      str ++= ":"
    }
//    _ind = followUp(tmpl.value, str, _ind, rules, params)
    _ind
  }

  def genEntityValue: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangEntityValue]
    if (tmpl.name.isDefined) str ++= tmpl.name.get.toString

    val noEOS = params.copy(addEOS = false)
    if (tmpl.params.isDefined) {
      str ++= "("
      tmpl.params.get.zipWithIndex.foreach { case (attr, i) =>
        _ind = followUp(attr, str, _ind, rules, noEOS)
        if (i != tmpl.params.get.size - 1) str ++= ","
      }
      str ++= ")"
    } else if (tmpl.attrs.isDefined) {
      str ++= "{"
      tmpl.attrs.get.zipWithIndex.foreach { case (attr, i) =>
        _ind = followUp(attr, str, _ind, rules, noEOS)
        if (i != tmpl.attrs.get.size - 1) str ++= ","
      }
      str ++= "}"
    } else {
      str ++= "("
      str ++= ")"
    }

    _ind
  }

  def genAttribute: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangAttribute]
//    if (tmpl.`type`.isDefined) str ++= tmpl.`type`.get.toString ++= ":"
//    if (tmpl.attr.isDefined) {
//      str ++= tmpl.attr.get.toString
//      str ++= ":"
//    }
//    _ind = followUp(tmpl.value, str, _ind, rules, params)
    _ind
  }

  def genArrayValue: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    var _ind = indent
    val tmpl = node.asInstanceOf[LangArrayValue]
    if (tmpl.`type`.isDefined) _ind = followUp(tmpl.`type`.get, str, _ind, rules, params)
    str ++= "["
    if (tmpl.params.isDefined) tmpl.params.get.zipWithIndex.foreach { case (param, i) =>
      _ind = followUp(param, str, _ind, rules, params)
      if (i != tmpl.params.get.size - 1) str ++= ","
    }
    str ++= "]"
    _ind
  }

  def genStringValue: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    val tmpl = node.asInstanceOf[LangStringValue]
    str ++= "\""
    str ++= tmpl.value.toString
    str ++= "\""
    indent
  }

  def genTextValue: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    val tmpl = node.asInstanceOf[LangTextValue]
    str ++= "\""
    str ++= tmpl.value.toString
    str ++= "\""
    indent
  }

  def genLongValue: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    val tmpl = node.asInstanceOf[LangLongValue]
    str ++= tmpl.value.toString
    indent
  }

  def genDoubleValue: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    val tmpl = node.asInstanceOf[LangDoubleValue]
    str ++= tmpl.value.toString
    indent
  }

  def genBoolValue: BlockGenerator = (node: AstTmplNode, str: StringBuilder, indent: Indent, rules: List[FormatRule], params: GenParameter, followUp: (AstTmplNode, StringBuilder, Indent, List[FormatRule], GenParameter) => Indent) => {
    val tmpl = node.asInstanceOf[LangBoolValue]
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
