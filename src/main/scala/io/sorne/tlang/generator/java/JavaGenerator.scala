package io.sorne.tlang.generator.java

import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.call._
import io.sorne.tlang.ast.tmpl.condition.TmplConditionBlock
import io.sorne.tlang.ast.tmpl.func.TmplFunc
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
      case expr: TmplExpression => genExpression(expr)
      case func: TmplFunc => genFunc(func)
      case impl: TmplImpl => genImpl(impl)
    }
  }

  def genImpl(impl: TmplImpl): String = {
    val str = new StringBuilder
    str ++= genAnnotations(impl.annots)
    str ++= impl.props.fold("public class")(prop => genProps(prop)) ++= " " ++= impl.name ++= " {\n"
    if (impl.content.isDefined) str ++= genContents(impl.content.get)
    str ++= "\n}\n\n"
    str.toString()
  }

  def genFunc(func: TmplFunc): String = {
    val str = new StringBuilder
    str ++= genAnnotations(func.annots)
    str ++= genOptionalProps(func.props, addSpace = true)
    str ++= func.ret.fold("void")(multi => genValueType(multi.values.head)) ++= " "
    str ++= func.name ++= "("
    if (func.curries.isDefined) {
      func.curries.get.head.params.foreach(params => params.map(genParam).mkString(", "))
    }
    str ++= ")"
    if (func.content.isDefined) {
      str ++= " {\n"
      str ++= "\n}\n"
    }
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
    str ++= `type`.name
    str ++= genGeneric(`type`.generic)
    str.toString()
  }

  def genGeneric(gen: Option[TmplGeneric]): String = {
    if (gen.isDefined) {
      val str = new StringBuilder
      str ++= "<" ++ gen.get.types.map(genType).mkString(", ") ++= ">"
      str.toString()
    } else ""
  }

  def genExpression(expr: TmplExpression): String = {
    expr match {
      case call: TmplCallObj => genCallObj(call)
      case func: TmplFunc => genFunc(func)
      case valueType: TmplValueType => genValueType(valueType)
      case variable: TmplVar => genVar(variable)
    }
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
    str ++= array.name ++= "[" ++= genValueType(array.elem) ++= "]"
    str.toString()
  }

  def genCallFunc(func: TmplCallFunc): String = {
    val str = new StringBuilder
    str ++= func.name ++= "(" ++= ")"
    str.toString()
  }

  def genCallVar(variable: TmplCallVar): String = variable.name

  def genVar(variable: TmplVar): String = {
    val str = new StringBuilder
    str ++= genType(variable.`type`) ++= " " ++= variable.name ++= " = " + genExpression(variable.value) ++= ";\n"
    str.toString()
  }

  def genValueType(valueType: TmplValueType): String = {
    valueType match {
      case call: TmplCallObj => genCallObj(call)
      case condition: TmplConditionBlock => genConditionBlock(condition)
      case primitive: TmplPrimitiveValue => genPrimitive(primitive)
    }
  }

  def genConditionBlock(block: TmplConditionBlock): String = {
    ""
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
