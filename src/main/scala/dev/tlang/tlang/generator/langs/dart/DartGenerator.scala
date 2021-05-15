package dev.tlang.tlang.generator.langs.dart

import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.loop.TmplFor
import dev.tlang.tlang.ast.tmpl.primitive.{TmplArrayValue, TmplEntityValue}
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.langs.style.CStyle

class DartGenerator extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = DartGenerator.genBlock(tmpl)
}

object DartGenerator extends CStyle {

  override def genPackage(pkg: Option[TmplPkg]): String = ""

  override def genIncludes(uses: Option[List[TmplUse]]): String = {
    val str = new StringBuilder()
    uses.foreach(_.foreach(use => {
      str ++= includeKeyword() ++= " '" ++= use.parts.mkString("/").replaceFirst("/", ":").replace("/dart", ".dart") ++= "'"
      if (use.alias.isDefined) str ++= " as " ++= use.alias.get.toString
      str ++= comma() ++= "\n"
    }))
    str.toString
  }

  override def genArrayValue(array: TmplArrayValue): String = {
    val params = array.params.asInstanceOf[Option[List[TmplSetAttribute]]]
    if (params.isDefined && params.get.nonEmpty) genArrayValueParams(array.params.asInstanceOf[Option[List[TmplSetAttribute]]])
    else "[]"
  }

  override def genFor(forLoop: TmplFor): String = {
    val str = new StringBuilder
    str ++= "for("
    str ++= "var " ++= forLoop.variable.toString
    str ++= " " ++= genForType(forLoop.forType) ++= " "
    str ++= genOperation(forLoop.cond)
    str ++= ") "
    str ++= genExprContent(forLoop.content) ++= "\n"
    str.toString()
  }

  override def genAnonFunc(anonFunc: TmplAnonFunc): String = {
    val str = new StringBuilder
    str ++= genFuncCurry(anonFunc.currying)
    if (anonFunc.content.isInstanceOf[TmplExpression[_]]) str ++= " => "
    str ++= genExprContent(anonFunc.content)
    str.toString()
  }

  override  def genAttribute(attr: TmplAttribute): String = {
    val str = new StringBuilder
    if (attr.attr.isDefined) str ++= attr.attr.get.toString ++= ": "
    str ++= genOperation(attr.value)
    str.toString()
  }

  override def genEntityValue(entity: TmplEntityValue): String = {
    val str = new StringBuilder
    str ++= " new " ++= entity.name.getOrElse("").toString
    str ++= "("
    if (entity.params.isDefined) {
      str ++= entity.params.get.map(param => genAttribute(param.asInstanceOf[TmplAttribute])).mkString(",\n")
    }
    str ++= ")"
    if(entity.attrs.isDefined) {
      str ++= "{"
      entity.attrs.get.map(attr => genAttribute(attr.asInstanceOf[TmplAttribute])).mkString(",\n")
      str ++= "}"
    }
    str.toString()
  }

  override def commaRequired(): Boolean = true

  override def includeKeyword(): String = "import"

  override def packageKeyword(): String = ""

  override def defaultImplProps(): String = "class"

  override def defaultFuncProps(): String = ""

  override def genDefaultVarKeyword(): String = "var"
}