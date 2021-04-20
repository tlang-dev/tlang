package dev.tlang.tlang.generator.style

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.ast.tmpl.loop.{TmplDoWhile, TmplFor, TmplWhile}
import dev.tlang.tlang.ast.tmpl.primitive._

abstract class CStyle {

  def genBlock(tmpl: TmplBlock): String = {
    val str = new StringBuilder()
    str ++= genPackage(tmpl.pkg)
    str ++= genIncludes(tmpl.uses)
    tmpl.content.foreach(str ++= genContents(_))
    str.toString
  }

  def genPackage(pkg: Option[TmplPkg]): String = {
    val str = new StringBuilder()
    pkg.foreach(str ++= packageKeyword() ++= " " ++= _.parts.mkString(".") ++= comma() ++= "\n\n")
    str.toString
  }

  def genIncludes(uses: Option[List[TmplUse]]): String = {
    val str = new StringBuilder()
    uses.foreach(_.foreach(str ++= includeKeyword() ++= " " ++= _.parts.mkString(".") ++= comma() ++= "\n"))
    str.toString
  }

  def genContents(impls: List[TmplNode[_]]): String = {
    val str = new StringBuilder
    impls.foreach(str ++= genContent(_))
    str.toString()
  }

  def genContent(impl: TmplNode[_]): String = {
    impl match {
      case func: TmplFunc => genFunc(func)
      case expr: TmplExpression[_] => genExpression(expr)
      case impl: TmplImpl => genImpl(impl)
    }
  }

  def genImpl(impl: TmplImpl): String = {
    val str = new StringBuilder
    str ++= genAnnotations(impl.annots)
    str ++= impl.props.fold(defaultImplProps())(prop => genProps(prop)) ++= " " ++= impl.name.toString
    if (impl.fors.isDefined) {
      str ++= " " ++= impl.fors.get.props.fold("extends")(genProps(_)) ++= " "
      str ++= impl.fors.get.types.map(implFor => genType(implFor)).mkString(", ")
    }
    if (impl.withs.isDefined) {
      str ++= " " ++= impl.withs.get.props.fold("implements")(genProps(_)) ++= " "
      str ++= impl.fors.get.types.map(implFor => genType(implFor)).mkString(", ")
    }
    str ++= " {\n"
    if (impl.content.isDefined) str ++= genContents(impl.content.get)
    str ++= "\n}\n\n"
    str.toString()
  }

  def genFunc(func: TmplFunc): String = {
    val str = new StringBuilder
    str ++= genAnnotations(func.annots)
    str ++= func.props.fold(defaultFuncProps())(prop => genProps(prop)) ++= " "
    str ++= func.ret.fold("void")(ret => genType(ret.head)) ++= " "
    str ++= func.name.toString ++= "("
    if (func.curries.isDefined) {
      func.curries.get.head.params.foreach(params => str ++= params.map(genParam).mkString(", "))
    }
    str ++= ")"
    str ++= func.postPros.fold("")(prop => genProps(prop) + " ")
    if (func.content.isDefined) str ++= " " ++= genExprBlock(func.content.get) ++= "\n\n"
    else str ++= comma() ++= "\n\n"
    str.toString()
  }

  def genAnnotations(annots: Option[List[TmplAnnotation]]): String = {
    if (annots.isDefined) {
      val str = new StringBuilder
      annots.get.foreach(annot => {
        str ++= "@" ++= annot.name.toString
        if (annot.values.isDefined) {
          str ++= "("
          str ++= annot.values.get.map(value => value.name + " = " + genPrimitive(value.value)).mkString(", ")
          str ++= ")"
        }
        str ++= "\n"
      })
      str.toString()
    }
    else ""
  }

  def genOptionalProps(props: Option[TmplProp], addSpace: Boolean = false): String = {
    if (props.isDefined) genProps(props.get, addSpace)
    else ""
  }

  def genProps(props: TmplProp, addSpace: Boolean = false): String = {
    props.props.mkString(" ") + (
      if (addSpace) " "
      else "")
  }

  def genParam(param: TmplParam): String = {
    val str = new StringBuilder
    str ++= genType(param.`type`) ++= " " ++= param.name.toString
    str.toString()
  }

  def genType(`type`: TmplType): String = {
    val str = new StringBuilder
    str ++= `type`.name.toString
    str ++= genGeneric(`type`.generic)
    if (`type`.isArray) str ++= "[]"
    str.toString()
  }

  def genGeneric(gen: Option[TmplGeneric]): String = {
    if (gen.isDefined) {
      val str = new StringBuilder
      str ++= "<" ++ gen.get.types.map(genType).mkString(", ") ++= ">"
      str.toString()
    } else ""
  }

  def genExprContent(content: TmplExprContent[_], endOfStatement: Boolean = false, newLine: Boolean = false): String = {
    content match {
      case block: TmplExprBlock => genExprBlock(block)
      case expression: TmplExpression[_] => genExpression(expression, endOfStatement, newLine)
    }
  }

  def genExprBlock(block: TmplExprBlock): String = {
    val str = new StringBuilder
    str ++= "{\n" ++= block.exprs.map(b => genExpression(b, endOfStatement = true)).mkString("\n") ++= "\n}"
    str.toString()
  }

  def genExpression(expr: TmplExpression[_], endOfStatement: Boolean = false, newLine: Boolean = false): String = {
    expr match {
      case call: TmplCallObj => genEndOfStatement(genCallObj(call), endOfStatement, newLine)
      case func: TmplFunc => genFunc(func)
      case valueType: TmplValueType[_] => genValueType(valueType)
      case variable: TmplVar => genEndOfStatement(genVar(variable), endOfStatement, newLine)
      case ifStmt: TmplIf => genIf(ifStmt)
      case forLoop: TmplFor => genFor(forLoop)
      case whileLoop: TmplWhile => genWhile(whileLoop)
      case doWhile: TmplDoWhile => genDoWhile(doWhile)
//      case incl: TmplInclude => genInclude(incl)
      case ret: TmplReturn => genEndOfStatement(genReturn(ret), endOfStatement, newLine)
      case affect: TmplAffect => genEndOfStatement(genAffect(affect), endOfStatement, newLine)
    }
  }

  def genEndOfStatement(statement: String, endOfStatement: Boolean, newLine: Boolean): String = {
    var ret = statement
    if (endOfStatement) ret = ret + comma()
    if (newLine) ret = ret + "\n"
    ret
  }

//  def genInclude(include: TmplInclude): String = {
//    val str = new StringBuilder
//    include.results.foreach {
//      case Left(tLangStr) => str ++= tLangStr.getElement ++= "\n"
//      case Right(block) => str ++= genBlock(block.block)
//    }
//    str.toString()
//  }

  def genAffect(affect: TmplAffect): String = {
    val str = new StringBuilder
    str ++= genCallObj(affect.variable) ++= " = " ++= genOperation(affect.value)
    str.toString()
  }

  def genReturn(ret: TmplReturn): String = {
    val str = new StringBuilder
    str ++= "return " ++= genOperation(ret.operation)
    str.toString()
  }

  def genIf(ifStmt: TmplIf): String = {
    val str = new StringBuilder
    str ++= "if(" ++= genOperation(ifStmt.cond) ++= ") "
    str ++= genExprContent(ifStmt.content, ifStmt.content.isInstanceOf[TmplExpression[_]])
    if (ifStmt.elseBlock.isDefined) ifStmt.elseBlock.get match {
      case Left(elseBlock) => str ++= " else " ++= genExprContent(elseBlock, elseBlock.isInstanceOf[TmplExpression[_]])
      case Right(ifBlock) => str ++= " else " ++= genIf(ifBlock)
    }
    str ++= "\n"
    str.toString()
  }

  def genFor(forLoop: TmplFor): String = {
    val str = new StringBuilder
    str ++= "for(" ++= ") "
    str ++= genExprContent(forLoop.content) ++= "\n"
    str.toString()
  }

  def genWhile(whileLoop: TmplWhile): String = {
    val str = new StringBuilder
    str ++= "while(" ++= genOperation(whileLoop.cond) ++= ") "
    str ++= genExprContent(whileLoop.content, whileLoop.content.isInstanceOf[TmplExpression[_]]) ++= "\n"
    str.toString()
  }

  def genDoWhile(doWhile: TmplDoWhile): String = {
    val str = new StringBuilder
    str ++= "do " ++= genExprContent(doWhile.content, doWhile.content.isInstanceOf[TmplExpression[_]])
    str ++= " while(" ++= genOperation(doWhile.cond) ++= ");\n"
    str.toString()
  }

  def genCallObj(callObj: TmplCallObj): String = {
    val str = new StringBuilder
    str ++= callObj.calls.map(genCallObjType).mkString(".")
    str.toString()
  }

  def genCallObjType(objType: TmplCallObjType): String = {
    objType match {
      case array: TmplCallArray => genCallArray(array)
      case func: TmplCallFunc => genCallFunc(func)
      case variable: TmplCallVar => genCallVar(variable)
    }
  }

  def genCallArray(array: TmplCallArray): String = {
    val str = new StringBuilder
    str ++= array.name.toString ++= "[" ++= genOperation(array.elem) ++= "]"
    str.toString()
  }

  def genCallFunc(func: TmplCallFunc): String = {
    val str = new StringBuilder
    str ++= func.name.toString
    if (func.currying.isDefined) {
      func.currying.foreach(_.foreach(curry => {
        str ++= "("
        curry.params.foreach(str ++= _.map(attr => attr.name.fold("")(_.toString + ": ") + genOperation(attr.value)).mkString(", "))
        str ++= ")"
      }))
    } else str ++= "()"
    str.toString()
  }

  def genCallVar(variable: TmplCallVar): String = variable.name.toString

  def genVar(variable: TmplVar): String = {
    val str = new StringBuilder
    str ++= genAnnotations(variable.annots)
    variable.props.foreach(prop => str ++= genProps(prop, addSpace = true))
    if (variable.`type`.isDefined) str ++= genType(variable.`type`.get) ++= " "
    else str ++= genDefaultVarKeyword() ++= " "
    str ++= variable.name.toString
    variable.value.foreach(str ++= " = " + genOperation(_))
    str ++= comma() ++= "\n"
    str.toString()
  }

  def genValueType(valueType: TmplValueType[_]): String = {
    valueType match {
      case call: TmplCallObj => genCallObj(call)
      case primitive: TmplPrimitiveValue[_] => genPrimitive(primitive)
    }
  }

  def genSimpleValueType(valueType: TmplSimpleValueType[_]): String = {
    valueType match {
      case call: TmplCallObj => genCallObj(call)
      case value: TmplPrimitiveValue[_] => genPrimitive(value)
    }
  }

  def genOperation(block: TmplOperation): String = {
    val str = new StringBuilder
    block.content match {
      case Left(block) => str ++= "(" ++= genOperation(block) ++= ")"
      case Right(cond) => str ++= genExpression(cond)
    }
    if (block.next.isDefined) {
      str ++= " " ++= genOperator(block.next.get._1)
      str ++= " " ++= genOperation(block.next.get._2)
    }
    str.toString()
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

  def genPrimitive(primitive: TmplPrimitiveValue[_]): String = {
    primitive match {
      case string: TmplStringValue => genStringValue(string)
      case long: TmplLongValue => genLongValue(long)
      case double: TmplDoubleValue => genDoubleValue(double)
      case bool: TmplBoolValue => genBoolValue(bool)
      case array: TmplArrayValue => genArrayValue(array)
      case entity: TmplEntityValue => genEntityValue(entity)
    }
  }

  def genEntityValue(entity: TmplEntityValue): String = {
    val str = new StringBuilder
    str ++= " new " ++= entity.name.toString ++= "("
    entity.attrs.foreach(attrs => str ++= attrs.map(attr => genAttribute(attr.asInstanceOf[TmplAttribute])).mkString(",\n"))
    str ++= ")"
    str.toString()
  }

  def genAttribute(attr: TmplAttribute): String = {
    val str = new StringBuilder
    if (attr.`type`.isDefined) str ++= attr.`type`.get.toString ++= ": "
    str ++= genOperation(attr.value)
    str.toString()
  }

  def genArrayValue(array: TmplArrayValue): String = {
    val str = new StringBuilder
    str ++= " new "
    array.`type`.foreach(t => str ++= genType(t))
    str ++= "[]"
    str ++= genArrayValueParams(array.params)
    str.toString()
  }

  def genArrayValueParams(params: Option[List[TmplSetAttribute]]): String = {
    val str = new StringBuilder
    if (params.isDefined) {
      str ++= "{"
      str ++= params.get.map(genSetAttribute).mkString(", ")
      str ++= "}"
    }
    str.toString()
  }

  def genStringValue(string: TmplStringValue): String = "\"" + string.value + "\""

  def genLongValue(long: TmplLongValue): String = long.value.toString

  def genDoubleValue(double: TmplDoubleValue): String = double.value.toString

  def genBoolValue(bool: TmplBoolValue): String = if (bool.value) "true" else "false"

  def genSetAttribute(attr: TmplSetAttribute): String = {
    val str = new StringBuilder
    if (attr.name.isDefined) str ++= genTmplID(attr.name) ++= ": "
    str ++= genOperation(attr.value)
    str.toString()
  }

  def genTmplID(tmplId: Option[TmplID]): String = {
    if (tmplId.isDefined) tmplId.get match {
      case interp: TmplInterpretedID => interp.toString
      case str: TmplStringID => "\"" + str.toString + "\""
      case block: TmplBlockID => block.toString
    }
    else ""
  }

  def comma(): String = if (commaRequired()) ";" else ""

  def commaRequired(): Boolean

  def includeKeyword(): String

  def packageKeyword(): String

  def defaultImplProps(): String

  def defaultFuncProps(): String

  def genDefaultVarKeyword(): String
}
