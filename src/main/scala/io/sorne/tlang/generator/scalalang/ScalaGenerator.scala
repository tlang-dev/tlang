package io.sorne.tlang.generator.scalalang

import io.sorne.tlang.ast.common.call.ComplexValueStatement
import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.call._
import io.sorne.tlang.ast.tmpl.condition.{TmplCondition, TmplConditionBlock}
import io.sorne.tlang.ast.tmpl.func.TmplFunc
import io.sorne.tlang.ast.tmpl.primitive._
import io.sorne.tlang.generator.CodeGenerator

class ScalaGenerator extends CodeGenerator {

  override def generate(tmpl: TmplBlock): String = {
    val str = new StringBuilder()
    tmpl.pkg.foreach(str ++= "package " ++= _.parts.mkString(".") ++= "\n\n")
    tmpl.uses.foreach(_.foreach(str ++= "import " ++= _.parts.mkString(".") ++= "\n"))
    tmpl.content.foreach(str ++= ScalaGenerator.genBlocks(_))
    str.toString
  }

}

object ScalaGenerator {

  def genBlocks(content: List[TmplContent]): String = {
    val str = new StringBuilder
    content.foreach {
      case func: TmplFunc => str ++= ScalaImplFuncGenerator.gen(func)
      case impl: TmplImpl => str ++= genImpl(impl) ++ "\n\n"
      case expr: TmplExpression => str ++= genExpression(expr)
    }
    str.toString
  }

  def genImpl(impl: TmplImpl): String = {
    val str = new StringBuilder
    str ++= "class " ++= impl.name
    impl.fors.foreach(_.zipWithIndex.foreach {
      case (for1, 0) => str ++= " extends " ++= for1.name
      case (for1, 1) => str ++= " with " ++= for1.name
      case (for1, _) => str ++= ", " ++= for1.name
    })
    str ++= " {\n" ++= genImplContent(impl.content) ++= "}\n\n"
    str.toString
  }

  def genImplContent(content: Option[List[TmplContent]]): String = {
    val str = new StringBuilder
    content.foreach(_.foreach {
      case func: TmplFunc => str ++= ScalaImplFuncGenerator.gen(func)
      case impl: TmplImpl => str ++= genImpl(impl)
      case expr: TmplExpression => str ++= genExpression(expr)
    })
    str.toString
  }

  def genExpressions(exprs: Option[List[TmplExpression]]): String = {
    val str = new StringBuilder
    exprs.foreach(e => str ++= e.map(genExpression).mkString("\n"))
    str.toString
  }

  def genExpression(expr: TmplExpression): String = {
    expr match {
      case obj: TmplCallObj => genCallObject(obj)
      case func: TmplFunc => ScalaImplFuncGenerator.gen(func)
      case value: TmplValueType => genValueType(value)
      case variable: TmplVar => genVar(variable)
    }
  }

  def genVar(variable: TmplVar): String = {
    val str = new StringBuilder

    str.toString
  }

  def genCallObject(obj: TmplCallObj): String = {
    obj.calls.map(genCallObjectType).mkString(".")
  }

  def genCallObjectType(objType: TmplCallObjType): String = {
    objType match {
      case array: TmplCallArray => genCallArray(array)
      case func: TmplCallFunc => genCallFunc(func)
      case variable: TmplCallVar => genCallVar(variable)
    }
  }

  def genCallVar(variable: TmplCallVar): String = variable.name

  def genCallArray(array: TmplCallArray): String = {
    val str = new StringBuilder
    str ++= array.name ++= "(" ++= genValueType(array.elem) ++= ")"
    str.toString
  }

  def genCallFunc(func: TmplCallFunc): String = {
    val str = new StringBuilder
    str ++= func.name ++= genCallCurrying(func)
    str.toString
  }

  def genCallCurrying(func: TmplCallFunc): String = {
    val str = new StringBuilder
    func.currying.foreach(_.foreach(params => str ++= "(" ++= genSetAttributes(params.params) ++= ")"))
    str.toString
  }

  def genSetAttributes(attrs: Option[List[TmplSetAttribute]]): String = {
    val str = new StringBuilder
    attrs.foreach(a => str ++= a.map(genSetAttribute).mkString(", "))
    str.toString
  }

  def genSetAttribute(attr: TmplSetAttribute): String = {
    val str = new StringBuilder
    attr.name.foreach(str ++= _ ++= " = ")
    str ++= genValueType(attr.value)
    str.toString
  }

  def genComplexValueStatement(value: ComplexValueStatement[_]):String = {
    val str = new StringBuilder

    str.toString
  }

  def genValueType(value: TmplValueType): String = {
    value match {
      case cond: TmplConditionBlock => genConditionBlock(cond)
      case multi: TmplMultiValue => genMulti(multi)
      case callObj: TmplCallObj => genCallObject(callObj)
      case primitive: TmplPrimitiveValue => genPrimitive(primitive)
    }
  }

  def genConditionBlock(cond: TmplConditionBlock): String = {
    val str = new StringBuilder

    str.toString
  }

  def genCondition(cond: TmplCondition): String = {
    val str = new StringBuilder

    str.toString
  }

  def genMulti(multi: TmplMultiValue): String = {
    val str = new StringBuilder
    str ++= "(" ++= multi.values.map(genValueType).mkString(", ") ++= ")"
    str.toString
  }

  def genPrimitive(value: TmplPrimitiveValue): String = {
    value match {
      case string: TmplStringValue => genString(string)
      case double: TmplDoubleValue => genDouble(double)
      case long: TmplLongValue => genLong(long)
      case text: TmplTextValue => genText(text)
      case entity: TmplEntityValue => genEntity(entity)
      case bool: TmplBoolValue => genBool(bool)
      case array: TmplArrayValue => genArray(array)
    }
  }

  def genEntity(entity: TmplEntityValue): String = {
    val str = new StringBuilder

    str.toString
  }

  def genArray(array: TmplArrayValue): String = {
    val str = new StringBuilder

    str.toString
  }

  def genString(string: TmplStringValue): String = "\"" + string.value + "\""

  def genDouble(number: TmplDoubleValue): String = number.value.toString

  def genLong(long: TmplLongValue): String = long.value.toString

  def genText(text: TmplTextValue): String = "\"\"\"" + text.value + "\"\"\""

  def genBool(bool: TmplBoolValue): String = if (bool.value) "true" else "false"

}
