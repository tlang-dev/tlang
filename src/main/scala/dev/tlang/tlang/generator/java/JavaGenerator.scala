package dev.tlang.tlang.generator.java

import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.generator.CodeGenerator
import dev.tlang.tlang.generator.style.CStyle

class JavaGenerator extends CodeGenerator {
  override def generate(tmpl: TmplBlock): String = JavaGenerator.genBlock(tmpl)
}

object JavaGenerator extends CStyle {
  override def commaRequired(): Boolean = true

  override def includeKeyword(): String = "import"

  override def packageKeyword(): String = "package"

  override def defaultImplProps(): String = "public class"

  override def defaultFuncProps(): String = "public"
}
