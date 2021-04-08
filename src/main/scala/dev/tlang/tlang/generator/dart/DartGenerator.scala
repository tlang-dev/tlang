package dev.tlang.tlang.generator.dart

import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplPkg, TmplUse}
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.style.CStyle

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

  override def commaRequired(): Boolean = true

  override def includeKeyword(): String = "import"

  override def packageKeyword(): String = ""

  override def defaultImplProps(): String = "class"

  override def defaultFuncProps(): String = ""

  override def genDefaultVarKeyword(): String = "var"
}