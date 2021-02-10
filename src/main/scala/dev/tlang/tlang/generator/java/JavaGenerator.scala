package dev.tlang.tlang.generator.java

import io.sorne.tlang.ast.helper.{ConditionLink, ConditionType}
import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.call._
import io.sorne.tlang.ast.tmpl.condition.{TmplCondition, TmplConditionBlock}
import io.sorne.tlang.ast.tmpl.func.TmplFunc
import io.sorne.tlang.ast.tmpl.loop.{TmplDoWhile, TmplFor, TmplWhile}
import io.sorne.tlang.ast.tmpl.primitive._
import io.sorne.tlang.generator.CodeGenerator

class JavaGenerator extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = {
    val str = new StringBuilder()
    tmpl.pkg.foreach(str ++= "package " ++= _.parts.mkString(".") ++= ";\n\n")
    tmpl.uses.foreach(_.foreach(str ++= "import " ++= _.parts.mkString(".") ++= ";\n"))
    tmpl.content.foreach(str ++= JavaGenerator.genContents(_))
    str.toString
  }
}

object JavaGenerator {

  def genContents(impls: List[TmplContent]): String = {
    var str = new StringBuilder
    impls.foreach(str ++= genContent(_))
    str.toString()
  }

  def genContent(impl: TmplContent): String = {
    impl match {
      case func: TmplFunc => genFunc(func)
      case expr: TmplExpression => genExpression(expr)
      case impl: TmplImpl => genImpl(impl)
    }
  }

  def genImpl(impl: TmplImpl): String = {
    val str = new StringBuilder
    str ++= genAnnotations(impl.annots)
    str ++= impl.props.fold("public class")(prop => genProps(prop)) ++= " " ++= impl.name.toString ++= " {\n"
    if (impl.content.isDefined) str ++= genContents(impl.content.get)
    str ++= "\n}\n\n"
    str.toString()
  }

  def genFunc(func: TmplFunc): String = {
    val str = new StringBuilder
    str ++= genAnnotations(func.annots)
    str ++= func.props.fold("public")(prop => genProps(prop)) ++= " "
    str ++= func.ret.fold("void")(ret => genType(ret.head)) ++= " "
    str ++= func.name.toString ++= "("
    if (func.curries.isDefined) {
      func.curries.get.head.params.foreach(params => str ++= params.map(genParam).mkString(", "))
    }
    str ++= ")"
    if (func.content.isDefined) str ++= " " ++= genExprBlock(func.content.get)
    else str ++= ";"
    str.toString()
  }

  def genAnnotations(annots: Option[List[TmplAnnotation]]): String = {
    if (annots.isDefined) {
      val str = new StringBuilder
      annots.get.foreach(annot => {
        str ++= "@" ++= annot.name
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
    str ++= genType(param.`type`) ++= " " ++= param.name
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

  def genExprContent(content: TmplExprContent): String = {
    content match {
      case block: TmplExprBlock => genExprBlock(block)
      case expression: TmplExpression => genExpression(expression)
    }
  }

  def genExprBlock(block: TmplExprBlock): String = {
    val str = new StringBuilder
    str ++= "{\n" ++= block.exprs.map(genExpression).mkString("\n") ++= "\n}"
    str.toString()
  }

  def genExpression(expr: TmplExpression): String = {
    expr match {
      case call: TmplCallObj => genCallObj(call)
      case func: TmplFunc => genFunc(func)
      case valueType: TmplValueType => genValueType(valueType)
      case variable: TmplVar => genVar(variable)
      case ifStmt: TmplIf => genIf(ifStmt)
      case forLoop: TmplFor => genFor(forLoop)
      case whileLoop: TmplWhile => genWhile(whileLoop)
      case doWhile: TmplDoWhile => genDoWhile(doWhile)
    }
  }

  def genIf(ifStmt: TmplIf): String = {
    val str = new StringBuilder
    str ++= "if(" ++= genConditionBlock(ifStmt.cond) ++= ") "
    str ++= genExprContent(ifStmt.content)
    if (ifStmt.elseBlock.isDefined) ifStmt.elseBlock.get match {
      case Left(elseBlock) => str ++= " else " ++= genExprContent(elseBlock)
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
    str ++= "while(" ++= genConditionBlock(whileLoop.cond) ++= ") "
    str ++= genExprContent(whileLoop.content) ++= "\n"
    str.toString()
  }

  def genDoWhile(doWhile: TmplDoWhile): String = {
    val str = new StringBuilder
    str ++= "do " ++= genExprContent(doWhile.content)
    str ++= " while(" ++= genConditionBlock(doWhile.cond) ++= ");\n"
    str.toString()
  }

  def genCallObj(callObj: TmplCallObj): String = {
    val str = new StringBuilder
    str ++= callObj.calls.map(genCallObjType).mkString(".") ++= ";"
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
    str ++= array.name.toString ++= "[" ++= genValueType(array.elem) ++= "]"
    str.toString()
  }

  def genCallFunc(func: TmplCallFunc): String = {
    val str = new StringBuilder
    str ++= func.name.toString ++= "(" ++= ")"
    str.toString()
  }

  def genCallVar(variable: TmplCallVar): String = variable.name.toString

  def genVar(variable: TmplVar): String = {
    val str = new StringBuilder
    str ++= genAnnotations(variable.annots)
    variable.props.foreach(prop => str ++= genProps(prop, addSpace = true))
    str ++= genType(variable.`type`) ++= " " ++= variable.name.toString ++= " = " + genExpression(variable.value) ++= ";\n"
    str.toString()
  }

  def genValueType(valueType: TmplValueType): String = {
    valueType match {
      case call: TmplCallObj => genCallObj(call)
      case condition: TmplConditionBlock => genConditionBlock(condition)
      case primitive: TmplPrimitiveValue => genPrimitive(primitive)
    }
  }

  def genSimpleValueType(valueType: TmplSimpleValueType): String = {
    valueType match {
      case call: TmplCallObj => genCallObj(call)
      case value: TmplPrimitiveValue => genPrimitive(value)
    }
  }

  def genConditionBlock(block: TmplConditionBlock): String = {
    val str = new StringBuilder
    block.content match {
      case Left(block) => str ++= "(" ++= genConditionBlock(block) ++= ")"
      case Right(cond) => str ++= genCondition(cond)
    }
    str ++= genConditionLinkWithBlock(block.link, block.nextBlock)
    str.toString()
  }

  def genCondition(cond: TmplCondition): String = {
    val str = new StringBuilder
    str ++= genSimpleValueType(cond.statement1)
    if (cond.condition.isDefined) {
      str ++= " " ++= genConditionType(cond.condition.get) ++= " "
      str ++= genSimpleValueType(cond.statement2.get)
    }
    str ++= genConditionLinkWithBlock(cond.link, cond.nextBlock)
    str.toString()
  }

  def genConditionLinkWithBlock(link: Option[ConditionLink.condition], nextBlock: Option[TmplConditionBlock]): String = {
    val str = new StringBuilder
    if (link.isDefined) {
      str ++= " " ++= genConditionLink(link.get)
      nextBlock.foreach(block => str ++= " " ++= genConditionBlock(block))
    }
    str.toString()
  }

  def genConditionType(cond: ConditionType.condition): String = {
    cond match {
      case io.sorne.tlang.ast.helper.ConditionType.EQUAL => "=="
      case io.sorne.tlang.ast.helper.ConditionType.GREATER => ">"
      case io.sorne.tlang.ast.helper.ConditionType.LESSER => "<"
      case io.sorne.tlang.ast.helper.ConditionType.GREATER_OR_EQUAL => ">="
      case io.sorne.tlang.ast.helper.ConditionType.LESSER_OR_EQUAL => "<="
      case io.sorne.tlang.ast.helper.ConditionType.NOT_EQUAL => "!="
    }
  }

  def genConditionLink(link: ConditionLink.condition): String = {
    link match {
      case io.sorne.tlang.ast.helper.ConditionLink.OR => "||"
      case io.sorne.tlang.ast.helper.ConditionLink.AND => "&&"
    }
  }

  def genPrimitive(primitive: TmplPrimitiveValue): String = {
    primitive match {
      case string: TmplStringValue => genStringValue(string)
      case long: TmplLongValue => genLongValue(long)
      case double: TmplDoubleValue => genDoubleValue(double)
      case bool: TmplBoolValue => genBoolValue(bool)
      case array: TmplArrayValue => genArrayValue(array)
    }
  }

  def genArrayValue(array: TmplArrayValue): String = {
    val str = new StringBuilder
    str ++= " new "
    array.`type`.foreach(t => str ++= genType(t))
    str ++= "[]"
    if (array.params.isDefined) {
      str ++= " {"
      array.params.get.map(genSetAttribute).mkString(", ")
      str ++= "};\n"
    }
    str.toString()
  }

  def genStringValue(string: TmplStringValue): String = "\"" + string.value + "\""

  def genLongValue(long: TmplLongValue): String = long.value.toString

  def genDoubleValue(double: TmplDoubleValue): String = double.value.toString

  def genBoolValue(bool: TmplBoolValue): String = if (bool.value) "true" else "false"

  def genSetAttribute(attr: TmplSetAttribute): String = genValueType(attr.value)

}
