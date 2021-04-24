package dev.tlang.tlang.generator.langs.scalalang

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.TmplFunc
import dev.tlang.tlang.ast.tmpl.primitive._
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.langs.java.JavaGenerator.genOperator

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

  def genBlocks(content: List[TmplNode[_]]): String = {
    val str = new StringBuilder
    content.foreach {
      case func: TmplFunc => str ++= ScalaImplFuncGenerator.gen(func)
      case impl: TmplImpl => str ++= genImpl(impl) ++ "\n\n"
      case expr: TmplExpression[_] => str ++= genExpression(expr)
    }
    str.toString
  }

  def genImpl(impl: TmplImpl): String = {
    val str = new StringBuilder
    str ++= "class " ++= impl.name.toString
    impl.fors.foreach(_.types.zipWithIndex.foreach {
      case (for1, 0) => str ++= " extends " ++= for1.name.toString
      case (for1, 1) => str ++= " with " ++= for1.name.toString
      case (for1, _) => str ++= ", " ++= for1.name.toString
    })
    str ++= " {\n" ++= genImplContent(impl.content) ++= "}\n\n"
    str.toString
  }

  def genImplContent(content: Option[List[TmplNode[_]]]): String = {
    val str = new StringBuilder
    content.foreach(_.foreach {
      case func: TmplFunc => str ++= ScalaImplFuncGenerator.gen(func)
      case impl: TmplImpl => str ++= genImpl(impl)
      case expr: TmplExpression[_] => str ++= genExpression(expr)
    })
    str.toString
  }

  def genExpressions(exprs: Option[List[TmplExpression[_]]]): String = {
    val str = new StringBuilder
    exprs.foreach(e => str ++= e.map(genExpression).mkString("\n"))
    str.toString
  }

  def genExpression(expr: TmplExpression[_]): String = {
    expr match {
      case obj: TmplCallObj => genCallObject(obj)
      case func: TmplFunc => ScalaImplFuncGenerator.gen(func)
      case value: TmplValueType[_] => genValueType(value)
      case variable: TmplVar => genVar(variable)
    }
  }

  def genVar(variable: TmplVar): String = {
    val str = new StringBuilder

    str.toString
  }

  def genCallObject(obj: TmplCallObj): String = {
    obj.calls.map(call => genCallObjectType(call)).mkString(".")
  }

  def genCallObjectType(objType: TmplCallObjType[_]): String = {
    objType match {
      case array: TmplCallArray => genCallArray(array)
      case func: TmplCallFunc => genCallFunc(func)
      case variable: TmplCallVar => genCallVar(variable)
    }
  }

  def genCallVar(variable: TmplCallVar): String = variable.name.toString

  def genCallArray(array: TmplCallArray): String = {
    val str = new StringBuilder
    str ++= array.name.toString ++= "(" ++= genOperation(array.elem) ++= ")"
    str.toString
  }

  def genCallFunc(func: TmplCallFunc): String = {
    val str = new StringBuilder
    str ++= func.name.toString ++= genCallCurrying(func)
    str.toString
  }

  def genCallCurrying(func: TmplCallFunc): String = {
    val str = new StringBuilder
    func.currying.foreach(_.foreach(params => str ++= "(" ++= genSetAttributes(params.params.asInstanceOf[Option[List[TmplSetAttribute]]]) ++= ")"))
    str.toString
  }

  def genSetAttributes(attrs: Option[List[TmplSetAttribute]]): String = {
    val str = new StringBuilder
    attrs.foreach(a => str ++= a.map(genSetAttribute).mkString(", "))
    str.toString
  }

  def genSetAttribute(attr: TmplSetAttribute): String = {
    val str = new StringBuilder
    attr.name.foreach(str ++= _.toString ++= " = ")
    str ++= genOperation(attr.value)
    str.toString
  }

  def genComplexValueStatement(value: ComplexValueStatement[_]): String = {
    val str = new StringBuilder

    str.toString
  }

  def genValueType(value: TmplValueType[_]): String = {
    value match {
      case multi: TmplMultiValue => genMulti(multi)
      case callObj: TmplCallObj => genCallObject(callObj)
      case primitive: TmplPrimitiveValue[_] => genPrimitive(primitive)
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

  def genMulti(multi: TmplMultiValue): String = {
    val str = new StringBuilder
    str ++= "(" ++= multi.values.map(genValueType).mkString(", ") ++= ")"
    str.toString
  }

  def genPrimitive(value: TmplPrimitiveValue[_]): String = {
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
